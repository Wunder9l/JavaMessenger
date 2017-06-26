package track.messenger.messages;

/**
 * Created by artem on 18.04.17.
 */

import com.sun.org.apache.regexp.internal.RE;

/**
 * Простое текстовое сообщение
 */
public class StatusMessage extends Message {

    public enum StatusCode {
        OK, ERROR
    }

    public enum Reason {
        LOGIN, TEXT, CHAT_LIST, CHAT_HIST, NEW_CHAT, NEW_USER, ADD_USER_TO_CHAT, LEAVE_CHAT, UnknownType
    }

    private String message;
    private StatusCode statusCode;
    private Reason reason;

    public Reason getReason() {
        return reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public StatusMessage(Long userId, StatusCode statusCode, Reason reason, String message) {
        this.userId = userId;
        this.statusCode = statusCode;
        this.reason = reason;
        this.message = message;
        this.type = Type.MSG_STATUS;
    }

    @Override
    public String toString() {
        return "StatusMessage{" +
                "message='" + message + '\'' +
                ", statusCode=" + statusCode +
                ", reason=" + reason +
                '}';
    }
}
