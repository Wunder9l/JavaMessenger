package track.messenger.client.input.handlers;

import track.messenger.client.SessionClient;
import track.messenger.messages.CreateUserMessage;
import track.messenger.messages.Message;

/**
 * Created by artem on 22.06.17.
 */
public class CreateUserHandle extends InputHandle {
    @Override
    public Message handleInput(String line, SessionClient sessionClient) {
        if (isAuthorized(sessionClient)) {
            log.error("You are authorized need to exit from current account (type /exit)");
        } else {
            String[] tokens = line.split(" ");
            if (tokens.length == 2) {
                return new CreateUserMessage(tokens[0], tokens[1]);
            } else {
                log.error("To create an account you should enter exactly 3 tokens: " +
                                "/createuser {username} {password}, {} given",
                        tokens.length);
            }
        }
        return null;
    }
}
