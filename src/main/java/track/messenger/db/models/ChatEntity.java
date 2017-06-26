package track.messenger.db.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by artem on 10.06.17.
 */
@Entity
@Table(name = "chats")
@NamedQuery(name = "Chat.getAll", query = "SELECT c from ChatEntity c")
public class ChatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "chatEntities", fetch = FetchType.LAZY)
    private Set<UserEntity> userEntities;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "chatEntity", fetch = FetchType.LAZY)
    @OrderBy("datetime ASC")
    private List<MessageEntity> messageEntities;

    public ChatEntity() {
    }

    public List<MessageEntity> getMessageEntities() {
        return messageEntities;
    }

    public void setMessageEntities(List<MessageEntity> messageEntities) {
        this.messageEntities = messageEntities;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<UserEntity> getUserEntities() {
        return userEntities;
    }

    public void setUserEntities(Set<UserEntity> userEntities) {
        this.userEntities = userEntities;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean addUser(UserEntity userEntity) {
        if (null == containsUser(userEntity)){
            userEntities.add(userEntity);
            return true;
        }
        return false;
    }

    public boolean deleteUser(UserEntity userEntity) {
        UserEntity foundUser = containsUser(userEntity);
        if (null != foundUser){
            return userEntities.remove(foundUser);
        }
        return false;
    }

    private UserEntity containsUser(UserEntity userEntity) {
        if (null == userEntities) {
            userEntities = new HashSet<>();
        }
        for (UserEntity user : userEntities) {
            if (user.equals(userEntity)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "ChatEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", userEntities=" + userEntities +
                ", messageEntities=" + messageEntities +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChatEntity){
            return ((ChatEntity) obj).getId().equals(id);
        }
        return false;
    }
}
