package track.messenger.server.processors;

import track.messenger.db.models.UserEntity;
import track.messenger.db.services.ChatService;
import track.messenger.db.services.MessageService;
import track.messenger.db.services.UserService;
import track.messenger.messages.Message;
import track.messenger.messages.MessageWrapper;
import track.messenger.net.protocol.BinaryProtocol;
import track.messenger.net.protocol.ProtocolException;
import track.messenger.server.utils.Socket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by artem on 18.04.17.
 */
public abstract class Processor {
    protected static ChatService chatService = new ChatService();
    protected static UserService userService = new UserService();
    protected static MessageService messageService = new MessageService();

    protected static BinaryProtocol protocol = new BinaryProtocol();

    public abstract List<MessageWrapper> processMessage(Message message, Map<Long, Socket> userIdToSocketMap)
            throws ProtocolException;

    List<MessageWrapper> dispatchMessages(List<Message> messages, Map<Long, Socket> userIdToSocketMap)
            throws ProtocolException {
        List<MessageWrapper> outMessages = new ArrayList<>();
        for (Message message : messages) {
            Socket socket = userIdToSocketMap.get(message.getUserId());
            if (null != socket) {
                MessageWrapper messageWrapper = new MessageWrapper(
                        socket.socketId,
                        message.getUserId(),
                        protocol.encode(message));
                outMessages.add(messageWrapper);
            }
        }
        return outMessages;
    }

    protected List<UserEntity> parseUsers(String[] usernames) {
        List<UserEntity> users = new ArrayList<>(usernames.length);
        for (String name : usernames) {
            UserEntity dbUser = userService.getByUserame(name);
            if (null == dbUser) {
                users = null;
                break;
            } else {
                users.add(dbUser);
            }
        }
        return users;
    }
}
