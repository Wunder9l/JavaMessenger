package track.messenger.client.message.processors;

import track.messenger.client.SessionClient;
import track.messenger.client.entity.User;
import track.messenger.messages.ChatListResultMessage;
import track.messenger.messages.Message;
import track.messenger.messages.StatusMessage;

/**
 * Created by artem on 17.06.17.
 */
public class ChatListResultProcessor extends Processor {
    @Override
    public void processMessage(Message message, SessionClient session) {
        if (message instanceof ChatListResultMessage) {
            ChatListResultMessage resultMessage = (ChatListResultMessage) message;
            User user = session.getUser();
            user.setChatIds(resultMessage.getChatIds());
            user.showChats();
        }
    }
}
