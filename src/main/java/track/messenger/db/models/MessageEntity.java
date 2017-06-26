package track.messenger.db.models;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by artem on 10.06.17.
 */

@Entity
@Table(name = "messages")
@NamedQueries({
        @NamedQuery(name = "Message.getAllInChat",
                query = "SELECT m from MessageEntity m WHERE m.chatEntity.id = :chatId"),

})
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "message")
    private String message;

    @Column(name = "datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chat_id", nullable = false)
    private ChatEntity chatEntity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    public MessageEntity() {
    }

    public MessageEntity(UserEntity user, ChatEntity chat, String message) {
        this.userEntity = user;
        this.chatEntity = chat;
        this.message = message;
        this.datetime = new Date();
    }

    public ChatEntity getChatEntity() {
        return chatEntity;
    }

    public void setChatEntity(ChatEntity chatEntity) {
        this.chatEntity = chatEntity;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    @Override
    public String toString() {
        return "NetMessage{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", datetime=" + datetime +
                ", chatEntity=" + chatEntity.getName() +
                ", userEntity=" + userEntity.getUsername() +
                '}';
    }
}
