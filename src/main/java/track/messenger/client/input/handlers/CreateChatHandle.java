package track.messenger.client.input.handlers;

import track.messenger.client.SessionClient;
import track.messenger.messages.CreateChatMessage;
import track.messenger.messages.Message;
import track.messenger.utils.Validator;

/**
 * Created by artem on 22.06.17.
 */
public class CreateChatHandle extends InputHandle {
    @Override
    public Message handleInput(String line, SessionClient sessionClient) {
        if (isAuthorized(sessionClient) && (null != line)) {
            String[] tokens = line.split(" ");
            if (tokens.length == 1) {
                String[] usernames = parseUsernames(tokens[0]);
                if (null != usernames) {
                    return new CreateChatMessage(sessionClient.getUser().getUserId(), usernames, "");
                }
            } else if (tokens.length == 2) {
                String[] usernames = parseUsernames(tokens[1]);
                if (null != usernames) {
                    return new CreateChatMessage(sessionClient.getUser().getUserId(), usernames, tokens[0]);
                }
            }
            help();
        }
        return null;
    }

    private void help() {
        System.out.println("To create a new chat you should use command in format:\n" +
                "/newchat <chat_name> users:{name_0},{name_1},{name_N}\n" +
                "Pay attention that users should be entered without spaces");
    }
}
