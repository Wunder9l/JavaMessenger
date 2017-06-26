package track.messenger.client.message.processors;

import track.messenger.client.SessionClient;
import track.messenger.client.entity.ChatHistory;
import track.messenger.client.entity.User;
import track.messenger.messages.Message;
import track.messenger.messages.TextMessage;

import java.util.List;

/**
 * Created by artem on 20.06.17.
 */
public class TextProcessor extends Processor {

    @Override
    public void processMessage(Message message, SessionClient session) {
        if (isAuthorized(session)) {
            TextMessage textMessage = (TextMessage) message;
            User user = session.getUser();
            List<Long> chats = user.getChatIds();
            if (chats.contains(textMessage.getChatId())) {
                ChatHistory chatHistory = user.getChatHistory(textMessage.getChatId());
                if (null == chatHistory) {
                    session.processCommand("");
                }
            } else {
                log.error("Chat with ID {} not found", textMessage.getChatId());
            }
        }
    }
}
