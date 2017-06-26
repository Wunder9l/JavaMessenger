package track.messenger.client.input.handlers;

import track.messenger.client.SessionClient;
import track.messenger.messages.AddUsersToChatMessage;
import track.messenger.messages.CreateChatMessage;
import track.messenger.messages.Message;
import track.messenger.utils.Validator;

/**
 * Created by artem on 24.06.17.
 */
public class AddUsersToChatHandle extends InputHandle {
    @Override
    public Message handleInput(String line, SessionClient sessionClient) {
        if (isAuthorized(sessionClient)) {
            String[] tokens = line.split(" ");
            if (tokens.length == 2) {
                Long chatId;
                String chatName = null;
                try {
                    chatId = Long.parseLong(tokens[0]);
                } catch (NumberFormatException e) {
                    chatId = 0L;
                    chatName = tokens[0];
                }
                String[] usernames = parseUsernames(tokens[1]);
                if (null != usernames) {
                    return new AddUsersToChatMessage(
                            sessionClient.getUser().getUserId(),
                            usernames,
                            chatName,
                            chatId);
                }
            }
            help();
        }
        return null;
    }

    private void help() {
        System.out.println("To add users to existing chat you should use command in format:\n" +
                "/addtochat <chatId> users:{name_0},{name_1},{name_N} OR\n" +
                "/addtochat <chatname> users:{name_0},{name_1},{name_N}\n" +
                "Pay attention that users should be entered without spaces");
    }
}
