package track.messenger.server;

import track.messenger.server.utils.NetMessage;
import track.messenger.server.utils.MessageBuffer;
import track.messenger.messages.MessageWrapper;

import java.util.Queue;

/**
 * Created by jjenkov on 22-10-2015.
 * Modified by artem on 12.04.2017.
 */
public class WriteProxy {

    private MessageBuffer messageBuffer = null;
    private Queue writeQueue = null;

    public WriteProxy(MessageBuffer messageBuffer, Queue writeQueue) {
        this.messageBuffer = messageBuffer;
        this.writeQueue = writeQueue;
    }

    public NetMessage getMessage() {
        return this.messageBuffer.getNetMessage();
    }

    public boolean enqueue(MessageWrapper message) {
        return this.writeQueue.offer(message);
    }

}
