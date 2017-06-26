package track.messenger.client.input.handlers;

import track.messenger.client.SessionClient;
import track.messenger.messages.Message;
import track.messenger.messages.TextMessage;

/**
 * Created by artem on 14.06.17.
 */
public class TextHandle extends InputHandle {
    @Override
    public Message handleInput(String line, SessionClient sessionClient) {
        if (isAuthorized(sessionClient)) {
            String[] tokens = line.split(" ", 2);
            if (tokens.length == 2) {
                Long chatId = null;
                try {
                    chatId = Long.parseLong(tokens[0]);
                    if (sessionClient.doesChatExist(chatId)) {
                        return new TextMessage(sessionClient.getUser().getUserId(), chatId, tokens[1]);
                    } else {
                        log.error("Chat with specified ID does not exist! Please check id or update chats list");
                        return null;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            help();
        }
        return null;
    }

    private void help() {
        System.out.println("To send a message you should enter it in format: /text {chatId} {\"message in quotes\"}");
    }
}
