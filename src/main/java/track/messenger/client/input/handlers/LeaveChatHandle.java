package track.messenger.client.input.handlers;

import track.messenger.client.SessionClient;
import track.messenger.messages.AddUsersToChatMessage;
import track.messenger.messages.LeaveChatMessage;
import track.messenger.messages.Message;

/**
 * Created by artem on 25.06.17.
 */
public class LeaveChatHandle extends InputHandle {
    @Override
    public Message handleInput(String line, SessionClient sessionClient) {
        if (isAuthorized(sessionClient)) {
            String[] tokens = line.split(" ");
            if (tokens.length == 1) {
                Long chatId;
                String chatName = null;
                try {
                    chatId = Long.parseLong(tokens[0]);
                } catch (NumberFormatException e) {
                    chatId = 0L;
                    chatName = tokens[0];
                }
                return new LeaveChatMessage(
                        sessionClient.getUser().getUserId(),
                        chatName,
                        chatId);
            }
            help();
        }
        return null;
    }

    private void help() {
        System.out.println("To leave chat you should use command in format:\n" +
                "/leavechat <chatId> OR\n" +
                "/addtochat <chatname>");
    }
}
