package track.messenger.client.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by artem on 14.06.17.
 */
public class ChatMessage implements Serializable {
    // Do not save username here to avoid duplicating it in every message
    private Date datetime;
    private Long userId;
    private int index;     // all messages in chat has index for easy management (download from server)
    private String text;

    public ChatMessage() {
    }

    public ChatMessage(Date datetime, Long userId, int index, String text) {
        this.userId = userId;
        this.datetime = datetime;
        this.text = text;
        this.index = index;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "datetime=" + datetime +
                ", userId=" + userId +
                ", index=" + index +
                ", text='" + text + '\'' +
                '}';
    }
}
