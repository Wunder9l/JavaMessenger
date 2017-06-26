package track.messenger.messages;

import java.util.Arrays;

/**
 * Created by artem on 23.06.17.
 */
public class CreateChatMessage extends Message {
    String[] usernames;
    String chatName;

    public CreateChatMessage(Long userId, String[] usernames, String chatName) {
        this.type = Type.MSG_CHAT_CREATE;
        this.userId = userId;
        this.usernames = usernames;
        this.chatName = chatName;
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

    @Override
    public String toString() {
        return "CreateChatMessage{" +
                "usernames=" + Arrays.toString(usernames) +
                ", chatName='" + chatName + '\'' +
                '}';
    }
}
