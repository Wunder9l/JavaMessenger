import track.messenger.db.models.ChatEntity;
import track.messenger.db.models.MessageEntity;
import track.messenger.db.models.UserEntity;
import track.messenger.db.services.ChatService;
import track.messenger.db.services.MessageService;
import track.messenger.db.services.UserService;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * Created by artem on 12.06.17.
 */

public class DbTests {

    @Test
    public void addUsers()
    {
        UserService userService = new UserService();
        UserEntity alex = new UserEntity();
        alex.setUsername("Kate");
        alex.setPassword("Kate");
        UserEntity alexBd = userService.add(alex);
        System.out.println(alexBd);

        UserEntity kate = new UserEntity();
        kate.setUsername("Kate");
        kate.setPassword("Kate");
        UserEntity kateBd = userService.add(kate);
        System.out.println(kateBd);
    }

    @Test
    public void showAllUsers(){
        UserService userService = new UserService();
        System.out.println(userService.getAll());
    }

    @Test
    public void addChat()
    {
        ChatService chatService = new ChatService();
        UserService userService = new UserService();

//        ChatEntity chatEntity = chatService.get(5);
        ChatEntity chatEntity = new ChatEntity();
        chatEntity.setName("kate_eva");
        chatEntity = chatService.add(chatEntity);

        UserEntity kate = userService.getByUserame("Kate");
        UserEntity eva = userService.getByUserame("Eva");
        chatEntity.addUser(kate);
        chatEntity.addUser(eva);
        kate.addToChat(chatEntity);
        eva.addToChat(chatEntity);

        chatService.update(chatEntity);
        userService.update(kate);
        userService.update(eva);
        System.out.println(chatEntity);
    }

    @Test
    public void getChatByNameAndUserId(){
        ChatService chatService = new ChatService();
        ChatEntity chatEntity = chatService.getChatWithNameAndContainsUser("kate_eva", 2L);
        System.out.println(chatEntity);
    }

    @Test
    public void addMessage(){
        ChatService chatService = new ChatService();
        UserService userService = new UserService();
        MessageService messageService = new MessageService();
        ChatEntity chatEntity = chatService.get(1);
        UserEntity alex = userService.getByUserame("Alex");

        MessageEntity k2a = new MessageEntity();
        k2a.setChatEntity(chatEntity);
        k2a.setUserEntity(alex);
        k2a.setMessage("Hi, Kate");
        k2a.setDatetime(new Date());

        messageService.add(k2a);
    }

    @Test
    public void printMessage(){
        MessageService messageService = new MessageService();
        List<MessageEntity> chatMessageEntities = messageService.getAllInChat(1);
        System.out.println(chatMessageEntities);
    }

    @Test
    public void printChatMessages(){
        ChatService chatService = new ChatService();
        ChatEntity chat = chatService.get(1);
        List<MessageEntity>messageEntities = chat.getMessageEntities();
        System.out.println(messageEntities);
    }
}
