package track.messenger.messages;

/**
 * Created by artem on 25.06.17.
 */
public class LeaveChatMessage extends Message {
    String chatName;
    Long chatId;

    public LeaveChatMessage(Long userId, String chatName, Long chatId) {
        this.type = Type.MSG_LEAVE_CHAT;
        this.userId = userId;
        this.chatId = chatId;
        this.chatName = chatName;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
