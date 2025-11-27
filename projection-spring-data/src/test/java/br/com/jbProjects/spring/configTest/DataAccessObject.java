package br.com.jbProjects.spring.configTest;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by julio.bueno on 27/11/2025.
 */
@Repository
public class DataAccessObject {

    private final EntityManager entityManager;

    public DataAccessObject(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public void persist(Object entity){
        entityManager.persist(entity);
    }

    @Transactional
    public void remove(Object entity){
        Object id = getId(entity);
        Object managed = entityManager.getReference(entity.getClass(), id);
        entityManager.remove(managed);
    }

    private Object getId(Object entity){
        return entityManager.getEntityManagerFactory()
                .getPersistenceUnitUtil()
                .getIdentifier(entity);
    }
}
