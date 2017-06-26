package track.messenger.db.services;

import track.messenger.db.models.UserEntity;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by artem on 11.06.17.
 */
public class UserService extends BaseService {
    public UserEntity add(UserEntity userEntity) {
        em.getTransaction().begin();
        UserEntity userEntityFromDB = em.merge(userEntity);
        em.getTransaction().commit();
        return userEntityFromDB;
    }

    public void delete(long id) {
        em.getTransaction().begin();
        em.remove(get(id));
        em.getTransaction().commit();
    }

    public UserEntity get(long id) {
        return em.find(UserEntity.class, id);
    }

    public UserEntity getByUserame(String username) {
        try {
            TypedQuery<UserEntity> namedQuery = em.createNamedQuery("User.getByName", UserEntity.class).
                    setParameter("username", username);
            return namedQuery.getSingleResult();
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void update(UserEntity userEntity) {
        em.getTransaction().begin();
        em.merge(userEntity);
        em.getTransaction().commit();
    }

    public List<UserEntity> getAll() {
        TypedQuery<UserEntity> namedQuery = em.createNamedQuery("User.getAll", UserEntity.class);
        return namedQuery.getResultList();
    }
}
