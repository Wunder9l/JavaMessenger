package track.messenger.client.input.handlers;

import track.messenger.client.SessionClient;
import track.messenger.messages.Message;
import track.messenger.messages.TextMessage;

/**
 * Created by artem on 17.06.17.
 */
public class ChatHandle extends InputHandle {
    @Override
    public Message handleInput(String line, SessionClient sessionClient) {
        Long chatId = null;
        try {
            chatId = Long.parseLong(line);
//            return new TextMessage(chatId, line);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        help();
        return null;
    }

    private void help() {
        System.out.println("To pick a chat you should enter it in format: /chat {chatId}");
    }
}
