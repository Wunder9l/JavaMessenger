package track.messenger.client.input.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import track.messenger.client.SessionClient;
import track.messenger.client.entity.User;
import track.messenger.messages.Message;
import track.messenger.utils.Validator;

import javax.xml.parsers.FactoryConfigurationError;


/**
 * Created by artem on 14.06.17.
 */
public abstract class InputHandle {
    protected Logger log;

    public InputHandle() {
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    public abstract Message handleInput(String line, SessionClient sessionClient);

    protected boolean isAuthorized(SessionClient sessionClient) {
        if (null != sessionClient) {
            return sessionClient.isAuthorized();
        } else {
            log.error("Null- object of SessionClient");
        }
        return false;
    }

    protected String[] parseUsernames(String line) {
        if (line.startsWith("users:")) {
            String usersLine = line.substring("users:".length());
            String[] users = usersLine.split(",");
            if (0 < users.length) {
                for (String username : users) {
                    if (!isUsernameCorrect(username)) {
                        return null;
                    }
                }
                return users;
            } else {
                log.error("You should enter at least one user");
            }
        }
        return null;
    }

    protected boolean isUsernameCorrect(String name) {
        String error = Validator.validateUsername(name);
        if (null != error) {
            log.error(error);
            return false;
        } else {
            return true;
        }
    }
}
