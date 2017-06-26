package track.messenger.messages;

import track.messenger.client.entity.ChatMessage;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by artem on 21.06.17.
 */
public class ChatHistoryResultMessage extends Message implements Serializable {
    Long chatId;
    Long totalMessages;
    Map<Long, String> members;
    List<ChatMessage> messages;

    public ChatHistoryResultMessage(Long userId, Long chatId, Long totalMessages, Map<Long, String> members, List<ChatMessage> messages) {
        this.type = Type.MSG_CHAT_HIST_RESULT;
        this.userId = userId;
        this.chatId = chatId;
        this.totalMessages = totalMessages;
        this.members = members;
        this.messages = messages;
    }

    public ChatHistoryResultMessage() {
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getTotalMessages() {
        return totalMessages;
    }

    public void setTotalMessages(Long totalMessages) {
        this.totalMessages = totalMessages;
    }

    public Map<Long, String> getMembers() {
        return members;
    }

    public void setMembers(Map<Long, String> members) {
        this.members = members;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "ChatHistoryResultMessage{" +
                "chatId=" + chatId +
                ", totalMessages=" + totalMessages +
                ", members=" + members +
                ", messages=" + messages +
                '}';
    }
}
