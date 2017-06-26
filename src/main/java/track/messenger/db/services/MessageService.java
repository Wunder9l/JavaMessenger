package track.messenger.db.services;

import track.messenger.db.models.ChatEntity;
import track.messenger.db.models.MessageEntity;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by artem on 12.06.17.
 */
public class MessageService extends BaseService {

    public MessageEntity add(MessageEntity messageEntity) {
        em.getTransaction().begin();
        MessageEntity messageEntityFromDB = em.merge(messageEntity);
        em.getTransaction().commit();
        return messageEntityFromDB;
    }

    public void delete(long id) {
        em.getTransaction().begin();
        em.remove(get(id));
        em.getTransaction().commit();
    }

    public MessageEntity get(long id) {
        return em.find(MessageEntity.class, id);
    }

    public void update(MessageEntity messageEntity) {
        em.getTransaction().begin();
        em.merge(messageEntity);
        em.getTransaction().commit();
    }

    public List<MessageEntity> getAllInChat(ChatEntity chatEntity) {
        return getAllInChat(chatEntity.getId());
    }

    public List<MessageEntity> getAllInChat(long chatId) {
        TypedQuery<MessageEntity> namedQuery = em.createNamedQuery("Message.getAllInChat", MessageEntity.class).
                setParameter("chatId", chatId);
        return namedQuery.getResultList();
    }
}
