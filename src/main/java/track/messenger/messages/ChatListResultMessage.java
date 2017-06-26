package track.messenger.messages;

import java.util.List;

/**
 * Created by artem on 17.06.17.
 */
public class ChatListResultMessage extends Message {
    private List<Long> chatIds;

    public ChatListResultMessage(Long userId, List<Long> chatIds) {
        this.userId = userId;
        this.chatIds = chatIds;
        this.type = Type.MSG_CHAT_LIST;
    }

    public List<Long> getChatIds() {
        return chatIds;
    }

    public void setChatIds(List<Long> chatIds) {
        this.chatIds = chatIds;
    }
}
