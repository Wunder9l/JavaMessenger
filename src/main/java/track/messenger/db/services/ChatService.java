package track.messenger.db.services;

import track.messenger.db.models.ChatEntity;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by artem on 12.06.17.
 */
public class ChatService extends BaseService {

    public ChatEntity add(ChatEntity chatEntity) {
        em.getTransaction().begin();
        ChatEntity chatEntityFromDB = em.merge(chatEntity);
        em.getTransaction().commit();
        return chatEntityFromDB;
    }

    public void delete(long id) {
        em.getTransaction().begin();
        em.remove(get(id));
        em.getTransaction().commit();
    }

    public ChatEntity get(long id) {
        return em.find(ChatEntity.class, id);
    }

    public void update(ChatEntity chatEntity) {
        em.getTransaction().begin();
        em.merge(chatEntity);
        em.getTransaction().commit();
    }

    public List<ChatEntity> getAll() {
        TypedQuery<ChatEntity> namedQuery = em.createNamedQuery("Chat.getAll", ChatEntity.class);
        return namedQuery.getResultList();
    }

    public ChatEntity getChatWithNameAndContainsUser(String name, Long userId) {
        TypedQuery<ChatEntity> typedQuery = em.createQuery(
                "SELECT c FROM ChatEntity c JOIN c.userEntities u WHERE u.id = :userId AND c.name = :name",
                ChatEntity.class)
                .setParameter("userId", userId)
                .setParameter("name", name);
        try {
            return typedQuery.getSingleResult();
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ChatEntity getChatWithIdAdnContainsUser(Long chatId, Long userId) {
        TypedQuery<ChatEntity> typedQuery = em.createQuery(
                "SELECT c FROM ChatEntity c JOIN c.userEntities u WHERE u.id = :userId AND c.id = :chatId",
                ChatEntity.class)
                .setParameter("userId", userId)
                .setParameter("chatId", chatId);
        try {
            return typedQuery.getSingleResult();
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        }
    }
}
