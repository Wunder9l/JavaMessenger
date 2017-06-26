package track.messenger.messages;

/**
 * Created by artem on 24.06.17.
 */
public class AddUsersToChatMessage extends Message {
    String[] usernames;
    String chatName;
    Long chatId;

    public AddUsersToChatMessage(Long userId, String[] usernames, String chatName, Long chatId) {
        this.type = Type.MSG_ADD_USERS_TO_CHAT;
        this.userId = userId;
        this.usernames = usernames;
        this.chatId = chatId;
        this.chatName = chatName;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String[] getUsernames() {
        return usernames;
    }

    public void setUsernames(String[] usernames) {
        this.usernames = usernames;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }
}
