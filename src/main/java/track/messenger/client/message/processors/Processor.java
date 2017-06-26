package track.messenger.client.message.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import track.messenger.client.SessionClient;
import track.messenger.messages.Message;

/**
 * Created by artem on 14.06.17.
 */
public abstract class Processor {
    protected Logger log;

    public Processor() {
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    public abstract void processMessage(Message message, SessionClient session);

    protected boolean isAuthorized(SessionClient sessionClient) {
        if (null != sessionClient) {
            return sessionClient.isAuthorized();
        } else {
            log.error("Null- object of SessionClient");
        }
        return false;
    }
}
