package track.messenger.server.utils;

import track.messenger.messages.MessageWrapper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jjenkov on 24-10-2015.
 * Modified by artem on 12.04.2017.
 */
public class MessageWriter {

    private List<MessageWrapper> writeQueue = new ArrayList<>();
    private MessageWrapper messageInProgress = null;
    private int bytesWritten = 0;

    public MessageWriter() {
    }

    public void enqueue(MessageWrapper message) {
        if (this.messageInProgress == null) {
            this.messageInProgress = message;
        } else {
            this.writeQueue.add(message);
        }
    }

    public void write(Socket socket, ByteBuffer byteBuffer) throws IOException {
        byteBuffer.put(this.messageInProgress.messageRawData, this.bytesWritten,
                this.messageInProgress.messageRawData.length - this.bytesWritten);
        byteBuffer.flip();

        this.bytesWritten += socket.write(byteBuffer);
        byteBuffer.clear();

        if (bytesWritten >= this.messageInProgress.messageRawData.length) {
            this.bytesWritten = 0;
            if (this.writeQueue.size() > 0) {
                this.messageInProgress = this.writeQueue.remove(0);
            } else {
                this.messageInProgress = null;
            }
        }
    }

    public boolean isEmpty() {
        return this.writeQueue.isEmpty() && this.messageInProgress == null;
    }

}
