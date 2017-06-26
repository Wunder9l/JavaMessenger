package track.messenger.server.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by jjenkov on 24-10-2015
 * Modified by artem on 12.04.2017.
 */
public interface IMessageReader {

    public void init(MessageBuffer readMessageBuffer);

    public void read(Socket socket, ByteBuffer byteBuffer) throws IOException;

    public List<NetMessage> getMessages();



}
