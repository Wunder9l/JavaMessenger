package track.messenger.client;

import track.messenger.client.entity.User;
import track.messenger.net.protocol.ProtocolException;

import java.io.IOException;

/**
 * Created by artem on 13.06.17.
 */
public class SessionClient {
    private User user;
    private MessengerClient client;

    public SessionClient(MessengerClient client) {
        this.client = client;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean doesChatExist(Long chatId) {
        if (null != user) {
            return user.getChatIds().contains(chatId);
        }
        return false;
    }

    public boolean isAuthorized() {
        if (null != user) {
            return true;
        } else {
            System.out.println("You have not authorized");
            return false;
        }
    }

    public void processCommand(String line) {
        try {
            client.processInput(line);
        } catch (IOException | ProtocolException e) {
            e.printStackTrace();
        }
    }
}
