import track.messenger.db.models.ChatEntity;
import track.messenger.db.models.MessageEntity;
import track.messenger.db.models.UserEntity;
import track.messenger.db.services.ChatService;
import track.messenger.db.services.UserService;

import java.util.Date;

/**
 * Created by artem on 10.06.17.
 */
public class PersistenceMain {
//    static EntityManagerFactory emf = Persistence
//            .createEntityManagerFactory("JavaMessenger");
//    static EntityManager em = emf.createEntityManager();

    public static void main(final String[] args) throws Exception {
//        em.getTransaction().begin();
//        Persistence.createEntityManagerFactory(BaseService.ENTITY_UNIT_NAME);
        UserService userService = new UserService();
        ChatService chatService = new ChatService();

        UserEntity kate = userService.getByUserame("Kate");
        UserEntity alex = userService.get(1);
//        UserEntity user = new UserEntity();
//        user.setUsername("Kate");
//        user.setPassword("Kate");
//        user.setChatEntities(new HashSet<ChatEntity>());
//        UserEntity userBd = userService.add(user);

//        System.out.println(kate);
        //
        ChatEntity chatEntity = chatService.get(1);
//        ChatEntity chatEntity = new ChatEntity();
//        chatEntity.addUser(kate);
//        chatEntity.addUser(alex);
//
//        kate.addToChat(chatEntity);
//        alex.addToChat(chatEntity);
//
//        chatService.update(chatEntity);
//        userService.update(alex);
//        userService.update(kate);
        //        chatEntity = chatService.add(chatEntity);

//        Set<UserEntity> users = chatEntity.getUserEntities();
//        System.out.println(chatEntity);

        MessageEntity k2a = new MessageEntity();
        k2a.setChatEntity(chatEntity);
        k2a.setUserEntity(kate);
        k2a.setMessage("Hi, Alex");

        k2a.setDatetime(new Date());
        //
////        HashSet<ChatEntity> chats = new HashSet<ChatEntity>(new ChatEntity[]{chatEntity})
//        user.setChatEntities(new HashSet<ChatEntity>()); // добавление адреса студенту
//        em.persist(user);
//        em.flush();
//        em.getTransaction().commit();
//
//        em.close();
//        emf.close();
    }
}
