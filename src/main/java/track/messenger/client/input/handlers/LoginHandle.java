package track.messenger.client.input.handlers;

import track.messenger.client.SessionClient;
import track.messenger.messages.LoginMessage;
import track.messenger.messages.Message;

/**
 * Created by artem on 14.06.17.
 */
public class LoginHandle extends InputHandle {
    @Override
    public Message handleInput(String line, SessionClient sessionClient) {
        String[] tokens = line.split(" ");
        if (tokens.length == 2) {
            return new LoginMessage(tokens[0], tokens[1]);
        } else {
            log.error("To login you should enter exactly 3 tokens: /login {username} {password}, {} given",
                    tokens.length);
            return null;
        }
    }
}
