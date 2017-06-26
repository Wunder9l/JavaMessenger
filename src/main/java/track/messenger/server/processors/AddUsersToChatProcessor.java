package track.messenger.server.processors;

import track.messenger.db.models.ChatEntity;
import track.messenger.db.models.UserEntity;
import track.messenger.messages.*;
import track.messenger.net.protocol.ProtocolException;
import track.messenger.server.utils.Socket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by artem on 24.06.17.
 */
public class AddUsersToChatProcessor extends Processor {

    @Override
    public List<MessageWrapper> processMessage(Message message, Map<Long, Socket> userIdToSocketMap)
            throws ProtocolException {
        List<Message> messages = new ArrayList<>();
        AddUsersToChatMessage addUsersToChatMessage = (AddUsersToChatMessage) message;
        StatusMessage statusMessage = new StatusMessage(
                addUsersToChatMessage.getUserId(),
                StatusMessage.StatusCode.ERROR,
                StatusMessage.Reason.ADD_USER_TO_CHAT,
                null);
        ChatEntity chatEntity = null;
        if (0L != addUsersToChatMessage.getChatId()) {
            chatEntity = chatService.getChatWithIdAdnContainsUser(
                    addUsersToChatMessage.getChatId(),
                    addUsersToChatMessage.getUserId());
        } else if (null != addUsersToChatMessage.getChatName()) {
            chatEntity = chatService.getChatWithNameAndContainsUser(
                    addUsersToChatMessage.getChatName(),
                    addUsersToChatMessage.getUserId()
            );
        }
        if (null != chatEntity) {
            String error = tryToAddUsersToChat(addUsersToChatMessage, chatEntity);
            if (null == error) {
                statusMessage.setStatusCode(StatusMessage.StatusCode.OK);
                statusMessage.setMessage("Success");
            } else {
                statusMessage.setMessage(error);
            }
        } else {
            statusMessage.setMessage("Chat with such name or chat id does NOT exist");
        }
        messages.add(statusMessage);
        return dispatchMessages(messages, userIdToSocketMap);
    }

    private String tryToAddUsersToChat(AddUsersToChatMessage message, ChatEntity chatEntity) {
        String error = null;
        List<UserEntity> users = parseUsers(message.getUsernames());
        if (null != users) {
            for (UserEntity userEntity : users) {
                if (true == chatEntity.addUser(userEntity)) {
                    userEntity.addToChat(chatEntity);
                    userService.update(userEntity);
                }
            }
            chatService.update(chatEntity);
        } else {
            error = "Can not parse usernames";
        }
        return error;
    }
}
