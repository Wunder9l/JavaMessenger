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
 * Created by artem on 26.06.17.
 */
public class LeaveChatProcessor extends Processor {

    @Override
    public List<MessageWrapper> processMessage(Message message, Map<Long, Socket> userIdToSocketMap)
            throws ProtocolException {
        List<Message> messages = new ArrayList<>();
        LeaveChatMessage leaveChatMessage = (LeaveChatMessage) message;
        StatusMessage statusMessage = new StatusMessage(
                leaveChatMessage.getUserId(),
                StatusMessage.StatusCode.ERROR,
                StatusMessage.Reason.LEAVE_CHAT,
                null);
        ChatEntity chatEntity = null;
        if (0L != leaveChatMessage.getChatId()) {
            chatEntity = chatService.getChatWithIdAdnContainsUser(
                    leaveChatMessage.getChatId(),
                    leaveChatMessage.getUserId());
        } else if (null != leaveChatMessage.getChatName()) {
            chatEntity = chatService.getChatWithNameAndContainsUser(
                    leaveChatMessage.getChatName(), leaveChatMessage.getUserId()
            );
        }
        if (null != chatEntity) {
            String error = tryToLeaveChat(leaveChatMessage, chatEntity);
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

    private String tryToLeaveChat(LeaveChatMessage message, ChatEntity chatEntity) {
        String error = null;
        UserEntity user = userService.get(message.getUserId());
        if (null != user) {
            boolean chatDeleted = user.leaveChat(chatEntity);
            boolean userDeleted = chatEntity.deleteUser(user);
            if (chatDeleted && userDeleted) {
                userService.update(user);
                chatService.update(chatEntity);
            } else {
                error = "An error during deleting user from chat";
            }
        } else {
            error = "Can not get user with such userId";
        }
        return error;
    }
}
