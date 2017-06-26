package track.messenger.server.utils;

import track.messenger.server.WriteProxy;
import track.messenger.messages.MessageWrapper;
import track.messenger.net.protocol.ProtocolException;

/**
 * Created by jjenkov on 16-10-2015.
 */
public interface IMessageWrapperProcessor {

    public void process(MessageWrapper message, WriteProxy writeProxy)throws ProtocolException;

}
