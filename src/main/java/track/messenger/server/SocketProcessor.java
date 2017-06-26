package track.messenger.server;

import track.messenger.db.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import track.messenger.Constants;
import track.messenger.server.processors.*;
import track.messenger.server.processors.beforelogon.CreateUserProcessor;
import track.messenger.server.processors.beforelogon.LoginProcessor;
import track.messenger.server.processors.beforelogon.ProcessorBeforeLogon;
import track.messenger.server.utils.*;
import track.messenger.messages.*;
import track.messenger.net.protocol.BinaryProtocol;
import track.messenger.net.protocol.ProtocolException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;

/**
 * Created by jjenkov on 24-10-2015.
 * Modified by artem on 12.04.2017.
 */
public class SocketProcessor implements Runnable {

    private Queue<Socket> inboundSocketQueue = null;

    private MessageBuffer readMessageBuffer = null; //todo   Not used now - but perhaps will be later - to check for space in the buffer before reading from sockets
    private MessageBuffer writeMessageBuffer = null; //todo   Not used now - but perhaps will be later - to check for space in the buffer before reading from sockets (space for more to write?)

    private Queue<MessageWrapper> outboundMessageQueue = new LinkedList<>(); //todo use a better / faster queue.

    private Map<Long, Socket> socketIdToSocketMap = new HashMap<>();
    private Map<Long, Socket> userIdToSocketMap = new HashMap<>();

    private ByteBuffer readByteBuffer = ByteBuffer.allocate(Constants.MB);
    private ByteBuffer writeByteBuffer = ByteBuffer.allocate(Constants.MB);
    private Selector readSelector = null;
    private Selector writeSelector = null;

    private IMessageWrapperProcessor messageProcessor = new InnerMessageWrapperProcessor();
    private WriteProxy writeProxy = null;

    private MessageReader messageReader = new MessageReader();
    private long nextSocketId = 16 * 1024; //start incoming socket ids from 16K - reserve bottom ids for pre-defined sockets (servers).

    /**
     * Содержит список сокетов перешедших из разряда empty (без данных для записи) в разряд NonEmpty
     * (с данными для записи). В псоледствии все сокеты из этой очереди будут зарегистрированы в
     * селекторе с флагом OP_WRITE
     */
    private Set<Socket> emptyToNonEmptySockets = new HashSet<>();
    /**
     * Содержит список сокетов перешедших из разряда NonEmpty (с данными для записи) в разряд empty
     * (без данных для записи) . В псоледствии все сокеты из этой очереди будут удалены из селектора
     */
    private Set<Socket> nonEmptyToEmptySockets = new HashSet<>();


    public SocketProcessor(Queue<Socket> inboundSocketQueue,
                           MessageBuffer readMessageBuffer,
                           MessageBuffer writeMessageBuffer)
            throws IOException {
        this.inboundSocketQueue = inboundSocketQueue;

        this.readMessageBuffer = readMessageBuffer;
        this.writeMessageBuffer = writeMessageBuffer;
        this.writeProxy = new WriteProxy(writeMessageBuffer, this.outboundMessageQueue);

        this.readSelector = Selector.open();
        this.writeSelector = Selector.open();
    }

    public void run() {
        while (true) {
            try {
                executeCycle();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(Constants.SERVER_SLEEP_TIME_IN_MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void executeCycle() throws IOException {
        takeNewSockets();
        readFromSockets();
        writeToSockets();
    }


    public void takeNewSockets() throws IOException {
        Socket newSocket = this.inboundSocketQueue.poll();

        while (newSocket != null) {
            newSocket.socketId = this.nextSocketId++;
            newSocket.socketChannel.configureBlocking(false);

            newSocket.messageWriter = new MessageWriter();

            this.socketIdToSocketMap.put(newSocket.socketId, newSocket);

            SelectionKey key = newSocket.socketChannel.register(this.readSelector, SelectionKey.OP_READ);
            key.attach(newSocket);

            newSocket = this.inboundSocketQueue.poll();
        }
    }


    public void readFromSockets() throws IOException {
        int readReady = this.readSelector.selectNow();

        if (readReady > 0) {
            Set<SelectionKey> selectedKeys = this.readSelector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                // TODO: check if we process all sockets (not miss the first)
                // may be it would be better to go through all the items and clear at the end?
                SelectionKey key = keyIterator.next();

                readFromSocket(key);

                keyIterator.remove();
            }
            selectedKeys.clear();
        }
    }

    private void readFromSocket(SelectionKey key) throws IOException {
        Socket socket = (Socket) key.attachment();
        MessageWrapper messageWrapper = messageReader.read(socket, this.readByteBuffer);
        if (null != messageWrapper) {
            try {
                this.messageProcessor.process(messageWrapper, this.writeProxy);  //the message processor will eventually push outgoing messages into an IMessageWriter for this socket.
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
        }

        if (socket.endOfStreamReached) {
            System.out.println("Socket closed: " + socket.socketId);
            this.socketIdToSocketMap.remove(socket.socketId);
            this.userIdToSocketMap.remove(socket.userId);
            key.attach(null);
            key.cancel();
            key.channel().close();
        }
    }


    public void writeToSockets() throws IOException {

        // Take all new messages from outboundMessageQueue
        takeNewOutboundMessages();

        // Cancel all sockets which have no more data to write.
        cancelEmptySockets();

        // Register all sockets that *have* data and which are not yet registered.
        registerNonEmptySockets();

        // Select from the Selector.
        int writeReady = this.writeSelector.selectNow();

        if (writeReady > 0) {
            Set<SelectionKey> selectionKeys = this.writeSelector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();

                Socket socket = (Socket) key.attachment();

                socket.messageWriter.write(socket, this.writeByteBuffer);

                if (socket.messageWriter.isEmpty()) {
                    this.nonEmptyToEmptySockets.add(socket);
                }

                keyIterator.remove();
            }

            selectionKeys.clear();

        }
    }

    private void registerNonEmptySockets() throws ClosedChannelException {
        for (Socket socket : emptyToNonEmptySockets) {
            socket.socketChannel.register(this.writeSelector, SelectionKey.OP_WRITE, socket);
        }
        emptyToNonEmptySockets.clear();
    }

    private void cancelEmptySockets() {
        for (Socket socket : nonEmptyToEmptySockets) {
            SelectionKey key = socket.socketChannel.keyFor(this.writeSelector);

            key.cancel();
        }
        nonEmptyToEmptySockets.clear();
    }

    private void takeNewOutboundMessages() {
        MessageWrapper outMessage = this.outboundMessageQueue.poll();
        while (outMessage != null) {
            Socket socket;
            if (0 != outMessage.userId) {
                socket = this.userIdToSocketMap.get(outMessage.userId);
            } else {
                socket = this.socketIdToSocketMap.get(outMessage.socketId);
            }

            if (socket != null) {
                MessageWriter messageWriter = socket.messageWriter;
                if (messageWriter.isEmpty()) {
                    messageWriter.enqueue(outMessage);
                    nonEmptyToEmptySockets.remove(socket);
                    emptyToNonEmptySockets.add(socket);    //not necessary if removed from nonEmptyToEmptySockets in prev. statement.
                } else {
                    messageWriter.enqueue(outMessage);
                }
            }

            outMessage = this.outboundMessageQueue.poll();
        }
    }

    class InnerMessageWrapperProcessor implements IMessageWrapperProcessor {
        Logger log = LoggerFactory.getLogger(InnerMessageWrapperProcessor.class);
        BinaryProtocol protocol = new BinaryProtocol();
        UserService userService = new UserService();

        //<editor-fold desc="Обработчики сообщений различного типа">
        private Map<Type, Processor> processors = new HashMap<Type, Processor>() {
            {
                put(Type.MSG_TEXT, new TextProcessor());
                put(Type.MSG_CHAT_LIST, new ChatListProcessor());
                put(Type.MSG_CHAT_HIST, new ChatHistProcessor());
                put(Type.MSG_CHAT_CREATE, new CreateChatProcessor());
                put(Type.MSG_ADD_USERS_TO_CHAT, new AddUsersToChatProcessor());
                put(Type.MSG_LEAVE_CHAT, new LeaveChatProcessor());
            }
        };

        private Map<Type, ProcessorBeforeLogon> processorsBeforeLogon = new HashMap<Type, ProcessorBeforeLogon>() {
            {
                put(Type.MSG_LOGIN, new LoginProcessor());
                put(Type.MSG_USER_CREATE, new CreateUserProcessor());
            }
        };
        //</editor-fold>

        @Override
        public void process(MessageWrapper messageWrapper, WriteProxy proxy) throws ProtocolException {
            if (isValidMessage(messageWrapper)) {
                if (processorsBeforeLogon.containsKey(messageWrapper.message.getType())) {
                    ProcessorBeforeLogon processorForMessage = processorsBeforeLogon.get(messageWrapper.message.getType());
                    processorForMessage.dispatchMessage(messageWrapper, proxy, userIdToSocketMap, socketIdToSocketMap);
                } else if (processors.containsKey(messageWrapper.message.getType())) {
                    Processor processorForMessage = processors.get(messageWrapper.message.getType());
                    List<MessageWrapper> outMessages;
                    try {
                        outMessages = processorForMessage.processMessage(messageWrapper.message, userIdToSocketMap);
                    } catch (ProtocolException e) {
                        log.error(e.getMessage());
                        return;
                    }
                    dispatchMessages(outMessages, proxy);
                } else {
                    String errorText = "Invalid message type: " + messageWrapper.message.getType();
                    messageWrapper.messageRawData = protocol.encode(
                            new StatusMessage(new Long(0),
                                    StatusMessage.StatusCode.ERROR,
                                    StatusMessage.Reason.UnknownType,
                                    errorText));
                    proxy.enqueue(messageWrapper);
                    log.error(errorText);
                }
            } else {
                String errorText = "Unauthorized access denied";
                messageWrapper.messageRawData = protocol.encode(
                        new StatusMessage(new Long(0),
                                StatusMessage.StatusCode.ERROR,
                                StatusMessage.Reason.UnknownType,
                                errorText));
                proxy.enqueue(messageWrapper);
                log.error(errorText);
            }
        }

        private boolean isValidMessage(MessageWrapper messageWrapper) {
            if (!processorsBeforeLogon.containsKey(messageWrapper.message.getType())) {
                Long userId = messageWrapper.message.getUserId();
                return (userIdToSocketMap.containsKey(userId) &&
                        userIdToSocketMap.get(userId).socketId == messageWrapper.socketId);
            } else {
                return true;
            }
        }

        private void dispatchMessages(List<MessageWrapper> outMessages, WriteProxy proxy) {
            for (MessageWrapper message : outMessages) {
                proxy.enqueue(message);
            }
        }
    }
}
