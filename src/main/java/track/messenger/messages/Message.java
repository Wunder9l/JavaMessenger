package track.messenger.messages;

import java.io.Serializable;

/**
 *
 */
public abstract class Message implements Serializable {
    /**
     * userId - содержит id пользователя (отправителя в случае клиент->сервер
     * и получателя в противном случае
     */
    protected Long userId;
    protected Type type;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
