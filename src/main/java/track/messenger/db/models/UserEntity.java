package track.messenger.db.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by artem on 10.06.17.
 */
@Entity
@Table(name = "users")
@NamedQueries({
        @NamedQuery(name = "User.getAll", query = "SELECT u from UserEntity u"),
        @NamedQuery(name = "User.getByName", query = "SELECT u from UserEntity u WHERE u.username = :username")
})
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_chats",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "chat_id"))
    private Set<ChatEntity> chatEntities;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userEntity", fetch = FetchType.LAZY)
    private Set<MessageEntity> messageEntities;

    public UserEntity() {
    }

    public UserEntity(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Set<ChatEntity> getChatEntities() {
        return chatEntities;
    }

    public void setChatEntities(Set<ChatEntity> chatEntities) {
        this.chatEntities = chatEntities;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", chatEntities number =" + chatEntities.size() +
                ", messageEntities number =" + messageEntities.size() +
                '}';
    }

    public boolean addToChat(ChatEntity chatEntity) {
        if (null == containsChat(chatEntity)) {
            chatEntities.add(chatEntity);
            return true;
        }
        return false;
    }

    private ChatEntity containsChat(ChatEntity chatEntity) {
        if (null == chatEntities) {
            chatEntities = new HashSet<>();
        }
//        ChatService chatService = new ChatService();
//        return chatService.getChatWithIdAdnContainsUser(chatEntity.getId(), id);
        for (ChatEntity chat : chatEntities) {
            if (chat.equals(chatEntity)) {
                return chat;
            }
        }
        return null;
    }

    public boolean leaveChat(ChatEntity chatEntity) {
        ChatEntity foundChat = containsChat(chatEntity);
        if (null != foundChat) {
            return chatEntities.remove(foundChat);
        }
        return false;
    }

    public List<Long> getChatIds() {
        return chatEntities.stream().map(ChatEntity::getId).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserEntity) {
            return ((UserEntity) obj).getId().equals(id);
        }
        return false;
    }
}
