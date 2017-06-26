package track.messenger.client.input.handlers;

import track.messenger.client.SessionClient;
import track.messenger.messages.Message;

/**
 * Created by artem on 22.06.17.
 */
public class ExitHandle extends InputHandle {
    @Override
    public Message handleInput(String line, SessionClient sessionClient) {
        if (isAuthorized(sessionClient)) {
            log.info("You have successfully exited from  account {}", sessionClient.getUser().getUsername());
            sessionClient.setUser(null);
        }
        return null;
    }
}
