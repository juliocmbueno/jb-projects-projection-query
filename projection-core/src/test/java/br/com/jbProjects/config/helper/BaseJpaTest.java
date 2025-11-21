package br.com.jbProjects.config.helper;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

/**
 * Created by julio.bueno on 21/11/2025.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseJpaTest {

    protected EntityManager entityManager;

    @BeforeAll
    void beforeAll() {
        entityManager = JPAHelper.entityManager();
        onBeforeAll();
    }

    protected void onBeforeAll() {}

    @AfterAll
    void afterAll() {
        onAfterAll();
        JPAHelper.shutdown();
    }

    public void persist(Object entity){
        JPAHelper.persist(entity);
    }

    public void remove(Object entity){
        JPAHelper.remove(entity);
    }

    protected void onAfterAll() {}
}
