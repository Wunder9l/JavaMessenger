package track.messenger.client.entity;

import track.messenger.client.message.exceptions.InvalidUserId;
import track.messenger.messages.ChatHistoryResultMessage;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by artem on 14.06.17.
 */
public class ChatHistory {
    private Long chatId;
    private Long totalMessages;
    private Map<Long, String> members;
    private Map<Long, ChatMessage> messages;

    public ChatHistory(Long chatId, Long totalMessages, Map<Long, String> members, Map<Long, ChatMessage> messages) {
        this.chatId = chatId;
        this.totalMessages = totalMessages;
        this.members = members;
        this.messages = messages;
    }

    public ChatHistory(Long chatId) {
        this.chatId = chatId;
        this.totalMessages = 0L;
        this.messages = new TreeMap<>();
        this.members = new TreeMap<>();
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

    public Map<Long, ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(Map<Long, ChatMessage> messages) {
        this.messages = messages;
    }

    public void addMessage(ChatMessage message) throws InvalidUserId {
        if (members.containsKey(message.getUserId())) {
            if (!this.messages.containsKey((long) message.getIndex())) {
                this.messages.put((long) message.getIndex(), message);
            }
        } else {
            throw new InvalidUserId(String.format(
                    "No specified user ID (%s) in chat members\n" +
                            "Message: %s", message.getUserId(), message));
        }
    }

    public void updateChatHistory(ChatHistoryResultMessage resultMessage) throws InvalidUserId {
        this.members = resultMessage.getMembers();
        this.totalMessages = resultMessage.getTotalMessages();
        List<ChatMessage> messageList = resultMessage.getMessages();
        if (null != messageList) {
            for (ChatMessage message : messageList) {
                addMessage(message);
            }
        }
    }

    @Override
    public String toString() {
        return "ChatHistory{" +
                "chatId=" + chatId +
                ", totalMessages=" + totalMessages +
                ", members=" + members +
                ", messages=" + messages +
                '}';
    }
}
