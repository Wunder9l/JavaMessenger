package track.messenger.server.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import track.messenger.messages.Message;
import track.messenger.messages.MessageWrapper;
import track.messenger.net.protocol.BinaryProtocol;
import track.messenger.net.protocol.ProtocolException;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by artem on 17.04.17.
 */
public class MessageReader {
    private BinaryProtocol protocol = new BinaryProtocol();
    static Logger log = LoggerFactory.getLogger(MessageReader.class);

    public MessageReader() {
    }

    public MessageWrapper read(Socket socket, ByteBuffer byteBuffer) throws IOException {
        int bytesRead = socket.read(byteBuffer);
        byteBuffer.flip();

        try {
            Message message = protocol.decode(byteBuffer.array(), 0, byteBuffer.limit());
            byteBuffer.clear();
            return new MessageWrapper(socket.socketId, message);
        } catch (ProtocolException e) {
            log.error("Parsing message from: {}\nInfo: {}\n", socket.socketChannel, e.getMessage());
            return null;
        }
    }
}
