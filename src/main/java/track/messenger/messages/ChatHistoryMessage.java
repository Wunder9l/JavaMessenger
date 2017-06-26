package track.messenger.messages;

/**
 * Created by artem on 21.06.17.
 */
public class ChatHistoryMessage extends Message {
    public static final int DEFAULT_NUMBER_OF_LAST_MESSAGES = 10;
    int startIndex;
    int endIndex;
    int preferredNumberOfLastMessages;
    int totalMessages;
    int chatId;

    public ChatHistoryMessage(Long userId,
                              int chatId,
                              int totalMessages,
                              int startIndex,
                              int endIndex,
                              int preferredNumberOfLastMessages) {
        this.type = Type.MSG_CHAT_HIST;
        this.userId = userId;
        this.chatId = chatId;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.preferredNumberOfLastMessages = preferredNumberOfLastMessages;
        this.totalMessages = totalMessages;
    }


    public ChatHistoryMessage(Long userId, int chatId) {
        this(userId, chatId, 0, -1, -1, DEFAULT_NUMBER_OF_LAST_MESSAGES);
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public int getPreferredNumberOfLastMessages() {
        return preferredNumberOfLastMessages;
    }

    public void setPreferredNumberOfLastMessages(int preferredNumberOfLastMessages) {
        this.preferredNumberOfLastMessages = preferredNumberOfLastMessages;
    }

    public int getTotalMessages() {
        return totalMessages;
    }

    public void setTotalMessages(int totalMessages) {
        this.totalMessages = totalMessages;
    }
}
