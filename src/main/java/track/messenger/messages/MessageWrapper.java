package track.messenger.messages;

/**
 * Created by artem on 17.04.17.
 */
public class MessageWrapper {
    public long socketId; // the id of source socket or destination socket, depending on whether is going in or out.
    public long userId;
    public Message message;
    public byte[] messageRawData;

    public MessageWrapper(long socketId, Message message) {
        this.socketId = socketId;
        this.message = message;
    }

    public MessageWrapper(long socketId, byte[] messageRawData) {
        this.socketId = socketId;
        this.messageRawData = messageRawData;
    }


    public MessageWrapper(long socketId,  long userId,byte[] messageRawData) {
        this(socketId, messageRawData);
        this.userId = userId;
    }

    public MessageWrapper(long socketId, long userId, Message message){
        this(socketId, message);
        this.userId = userId;
    }
}
