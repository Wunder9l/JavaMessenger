package track.messenger.messages;

/**
 * Created by artem on 17.06.17.
 */
public class ChatsListMessage extends Message {
    public ChatsListMessage(Long userId){
        this.userId = userId;
        this.type = Type.MSG_CHAT_LIST;
    }
}
