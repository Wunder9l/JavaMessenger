package track.messenger.server.processors.beforelogon;

import track.messenger.db.services.UserService;
import track.messenger.messages.MessageWrapper;
import track.messenger.net.protocol.BinaryProtocol;
import track.messenger.net.protocol.ProtocolException;
import track.messenger.server.WriteProxy;
import track.messenger.server.utils.Socket;

import java.util.Map;

/**
 * Created by artem on 23.06.17.
 */
public abstract class ProcessorBeforeLogon {
    static protected UserService userService = new UserService();
    BinaryProtocol protocol = new BinaryProtocol();


    public abstract void dispatchMessage(MessageWrapper messageWrapper,
                                         WriteProxy proxy,
                                         Map<Long, Socket> userIdToSocketMap,
                                         Map<Long, Socket> socketIdToSocketMap) throws ProtocolException;
}
