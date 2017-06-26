package track.messenger.client.input.handlers;

import track.messenger.client.SessionClient;
import track.messenger.messages.ChatsListMessage;
import track.messenger.messages.Message;

/**
 * Created by artem on 17.06.17.
 */
public class ChatsListHandle extends InputHandle {
    @Override
    public Message handleInput(String line, SessionClient sessionClient) {
        if (isAuthorized(sessionClient)) {
            return new ChatsListMessage(sessionClient.getUser().getUserId());
        } else {
            return null;
        }
    }
}
