package track.messenger.server.processors;

import track.messenger.db.models.UserEntity;
import track.messenger.messages.*;
import track.messenger.net.protocol.ProtocolException;
import track.messenger.server.utils.Socket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by artem on 17.06.17.
 */
public class ChatListProcessor extends Processor {

    @Override
    public List<MessageWrapper> processMessage(Message message, Map<Long, Socket> userIdToSocketMap)
            throws ProtocolException {
        List<Message> messages = new ArrayList<>();
        ChatsListMessage chatsListMessage = (ChatsListMessage) message;
        UserEntity userEntity = userService.get(chatsListMessage.getUserId());
        if (null != userEntity) {
            messages.add(new ChatListResultMessage(userEntity.getId(), userEntity.getChatIds()));
        } else {
            messages.add(new StatusMessage(
                    chatsListMessage.getUserId(),
                    StatusMessage.StatusCode.ERROR,
                    StatusMessage.Reason.CHAT_LIST,
                    "No such user"));
        }
        return dispatchMessages(messages, userIdToSocketMap);
    }
}
