package track.messenger.client.message.processors;

import track.messenger.client.SessionClient;
import track.messenger.client.entity.ChatHistory;
import track.messenger.client.message.exceptions.InvalidUserId;
import track.messenger.messages.ChatHistoryResultMessage;
import track.messenger.messages.Message;

/**
 * Created by artem on 21.06.17.
 */
public class ChatHistoryResultProcessor extends Processor {
    @Override
    public void processMessage(Message message, SessionClient session) {
        if (isAuthorized(session)) {
            ChatHistoryResultMessage resultMessage = (ChatHistoryResultMessage) message;
            ChatHistory chatHistory = session.getUser().getChatHistory(resultMessage.getChatId());
            if (null != chatHistory) {
                try {
                    chatHistory.updateChatHistory(resultMessage);
                } catch (InvalidUserId invalidUserId) {
                    invalidUserId.printStackTrace();
                }
            } else {
                log.error("No chat with specified ID", resultMessage.getChatId());
            }
        }
    }
}
