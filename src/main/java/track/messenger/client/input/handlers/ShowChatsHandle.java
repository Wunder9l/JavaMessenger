package track.messenger.client.input.handlers;

import track.messenger.client.SessionClient;
import track.messenger.client.entity.User;
import track.messenger.messages.Message;

/**
 * Created by artem on 17.06.17.
 */
public class ShowChatsHandle extends InputHandle {
    @Override
    public Message handleInput(String line, SessionClient sessionClient) {
        if (isAuthorized(sessionClient)) {
            sessionClient.getUser().showChats();
        }
        return null;
    }
}
