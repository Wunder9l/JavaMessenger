package track.messenger.net.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import track.messenger.messages.Message;

import java.io.*;

/**
 * Created by artem on 17.04.17.
 */
public class BinaryProtocol implements Protocol {

    static Logger log = LoggerFactory.getLogger(BinaryProtocol.class);

    @Override
    public Message decode(byte[] bytes) throws ProtocolException {
        return decode(bytes, 0, bytes.length);
    }

    public Message decode(byte[] bytes, int offset, int length) throws ProtocolException {
        Message msg = null;
        log.info("decoding length: {}", length);
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes, offset, length));
            msg = (Message) ois.readObject();
            log.info("decoded: {}", msg);
            return msg;
        } catch (IOException e) {
            e.printStackTrace();
            throw new ProtocolException("Decode: IOException: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new ProtocolException("Decode: ClassNotFoundException: " + e.getMessage());
        }
    }

    @Override
    public byte[] encode(Message msg) throws ProtocolException {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(byteOutputStream);
            oos.writeObject(msg);
            log.info("encoded (length {}): {}", byteOutputStream.size(), msg);
            return byteOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ProtocolException("Encode: IOException: " + e.getMessage());
        }
    }
}
