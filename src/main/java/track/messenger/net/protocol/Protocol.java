package track.messenger.net.protocol;


import track.messenger.messages.Message;

/**
 *
 */
public interface Protocol {

    Message decode(byte[] bytes) throws ProtocolException;

    byte[] encode(Message msg) throws ProtocolException;

}
