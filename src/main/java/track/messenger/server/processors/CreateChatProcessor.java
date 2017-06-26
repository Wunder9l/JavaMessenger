package track.messenger.server.processors;

import track.messenger.db.models.ChatEntity;
import track.messenger.db.models.UserEntity;
import track.messenger.db.services.ChatService;
import track.messenger.db.services.UserService;
import track.messenger.messages.*;
import track.messenger.net.protocol.ProtocolException;
import track.messenger.server.utils.Socket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by artem on 23.06.17.
 */
public class CreateChatProcessor extends Processor {
    private ChatService chatService = new ChatService();
    private UserService userService = new UserService();

    @Override
    public List<MessageWrapper> processMessage(Message message, Map<Long, Socket> userIdToSocketMap)
            throws ProtocolException {
        List<Message> messages = new ArrayList<>();
        CreateChatMessage createChatMessage = (CreateChatMessage) message;
        StatusMessage statusMessage = new StatusMessage(
                createChatMessage.getUserId(),
                StatusMessage.StatusCode.ERROR,
                StatusMessage.Reason.NEW_CHAT,
                null);
        ChatEntity chatEntity = chatService.getChatWithNameAndContainsUser(
                createChatMessage.getChatName(), createChatMessage.getUserId()
        );
        if (null == chatEntity) {
            String error = tryToAddChat(createChatMessage);
            if (null == error) {
                statusMessage.setStatusCode(StatusMessage.StatusCode.OK);
                statusMessage.setMessage("Success");
            } else {
                statusMessage.setMessage(error);
            }
        } else {
            statusMessage.setMessage("Chat with such name is already exists");
        }
        messages.add(statusMessage);
        return dispatchMessages(messages, userIdToSocketMap);
    }

    private String tryToAddChat(CreateChatMessage message) {
        String error = null;
        ChatEntity chatEntity = new ChatEntity();
        chatEntity.setName(message.getChatName());
        chatEntity = chatService.add(chatEntity);
        List<UserEntity> users = parseUsers(message.getUsernames());
        if (null != users) {
            chatService.add(chatEntity);
            for (UserEntity userEntity : users) {
                chatEntity.addUser(userEntity);
                userEntity.addToChat(chatEntity);
                userService.update(userEntity);
            }
            chatService.update(chatEntity);
        } else {
            error = "Can not parse usernames";
        }
        return error;
    }
}
