package track.messenger.db.services;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

/**
 * Created by artem on 11.06.17.
 */
public class BaseService {
    public static final String ENTITY_UNIT_NAME = "JavaMessenger";

    public EntityManager em = Persistence.createEntityManagerFactory(ENTITY_UNIT_NAME).createEntityManager();
}
