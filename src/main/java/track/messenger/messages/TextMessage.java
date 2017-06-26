package track.messenger.messages;

import java.util.Date;
import java.util.Objects;

/**
 * Простое текстовое сообщение
 */
public class TextMessage extends Message {
    private String text;
    private Long chatId;
    private Date datetime;

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        TextMessage message = (TextMessage) other;
        return Objects.equals(text, message.text) && chatId.equals(message.chatId);
    }

    public TextMessage(Long userId, Long chatId, String text, Date datetime) {
        this.type = Type.MSG_TEXT;
        this.text = text;
        this.chatId = chatId;
        this.userId = userId;
        this.datetime = datetime;
    }

    public TextMessage(Long userId, Long chatId, String text) {
        this.type = Type.MSG_TEXT;
        this.text = text;
        this.chatId = chatId;
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        int result = text.hashCode();
        result = 31 * result + chatId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TextMessage{" +
                "text='" + text + '\'' +
                ", chatId=" + chatId +
                '}';
    }
}