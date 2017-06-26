package track.messenger.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import track.messenger.Constants;
import track.messenger.client.input.handlers.*;
import track.messenger.client.message.processors.*;
import track.messenger.messages.Message;
import track.messenger.messages.Type;
import track.messenger.net.protocol.BinaryProtocol;
import track.messenger.net.protocol.Protocol;
import track.messenger.net.protocol.ProtocolException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


/**
 *
 */
public class MessengerClient {


    /**
     * Механизм логирования позволяет более гибко управлять записью данных в лог (консоль, файл и тд)
     */
    static Logger log = LoggerFactory.getLogger(MessengerClient.class);

    /**
     * Протокол, хост и порт инициализируются из конфига
     */
    private Protocol protocol;
    private int port;
    private String host;
    private SessionClient session = new SessionClient(this);

    private Map<String, InputHandle> inputHandlers = new HashMap<String, InputHandle>() {
        {
            put("/login", new LoginHandle());
            put("/text", new TextHandle());
            put("/showchats", new ShowChatsHandle());
            put("/chatslist", new ChatsListHandle());
            put("/chat", new ChatHandle());
            put("/chathist", new ChatHistoryHandle());
            put("/newuser", new CreateUserHandle());
            put("/newchat", new CreateChatHandle());
            put("/addtochat", new AddUsersToChatHandle());
            put("/leavechat", new LeaveChatHandle());
            put("/exit", new ExitHandle());
        }
    };

    private Map<Type, Processor> netMessageProcessors = new HashMap<Type, Processor>() {
        {
            put(Type.MSG_STATUS, new StatusProcessor());
            put(Type.MSG_CHAT_LIST, new ChatListResultProcessor());
//            put(Type.MSG_TEXT, new TextProcessor());
            put(Type.MSG_CHAT_HIST_RESULT, new ChatHistoryResultProcessor());
        }
    };
    /**
     * С каждым сокетом связано 2 канала in/out
     */
    private InputStream in;
    private OutputStream out;

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void initSocket() throws IOException {
        Socket socket = new Socket(host, port);
        in = socket.getInputStream();
        out = socket.getOutputStream();

        /*
      Тред "слушает" сокет на наличие входящих сообщений от сервера
     */
        Thread socketListenerThread = new Thread(() -> {
            final byte[] buf = new byte[1024 * 64];
            log.info("Starting listener thread...");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // Здесь поток блокируется на ожидании данных
                    int read = in.read(buf, 0, buf.length);
                    if (read > 0) {

                        // По сети передается поток байт, его нужно раскодировать с помощью протокола
                        Message msg = protocol.decode(Arrays.copyOf(buf, read));
                        onMessage(msg);
                    }
                } catch (Exception e) {
                    log.error("Failed to process connection: {}", e);
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        });

        socketListenerThread.start();
    }

    /**
     * Реагируем на входящее сообщение
     */
    public void onMessage(Message msg) {
        log.info("NetMessage received: {}", msg);
        Type type = msg.getType();
        if (netMessageProcessors.containsKey(type)) {
            netMessageProcessors.get(type).processMessage(msg, session);
        } else {
            log.error("Unknown message type: " + type.toString());
        }
    }

    /**
     * Обрабатывает входящую строку, полученную с консоли
     * Формат строки можно посмотреть в вики проекта
     */
    public void processInput(String line) throws IOException, ProtocolException {
        line = line.trim().replaceAll("\n", "");
        String[] tokens = line.split(" ", 2);
        log.info("Tokens: {}", Arrays.toString(tokens));
        String cmdType = tokens[0];
        if (inputHandlers.containsKey(cmdType)) {
            Message message = inputHandlers.get(cmdType).handleInput((1 < tokens.length) ? tokens[1] : null, session);
            if (null != message) {
                send(message);
            }
        } else {
            log.error("Invalid input: " + line);
        }
    }

    /**
     * Отправка сообщения в сокет клиент -> сервер
     */
    public void send(Message msg) throws IOException, ProtocolException {
        log.info(msg.toString());
        out.write(protocol.encode(msg));
        out.flush(); // принудительно проталкиваем буфер с данными
    }

    public static void main(String[] args) throws Exception {

        MessengerClient client = new MessengerClient();
        client.setHost(Constants.DEFAULT_SERVER_ADDRESS);
        client.setPort(Constants.DEFAULT_SERVER_PORT);
        client.setProtocol(new BinaryProtocol());

        try {
            client.initSocket();

            // Цикл чтения с консоли
            Scanner scanner = new Scanner(System.in);
            System.out.println("$");
            while (true) {
                String input = scanner.nextLine();
                if ("q".equals(input)) {
                    return;
                }
                try {
                    client.processInput(input);
                } catch (ProtocolException | IOException e) {
                    log.error("Failed to process user input", e);
                }
            }
        } catch (Exception e) {
            log.error("Application failed.", e);
        } finally {
            if (client != null) {
                // TODO
//                client.close();
            }
        }
    }
}