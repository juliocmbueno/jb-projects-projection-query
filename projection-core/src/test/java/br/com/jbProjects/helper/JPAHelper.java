package br.com.jbProjects.helper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Created by julio.bueno on 21/11/2025.
 */
public class JPAHelper {

    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("testPU");
    private static EntityManager em = emf.createEntityManager();

    public static EntityManager entityManager() {
        if (em == null || !em.isOpen()) {

            if (emf == null || !emf.isOpen()) {
                emf = Persistence.createEntityManagerFactory("testPU");
            }

            em = emf.createEntityManager();
        }

        return em;
    }

    public static void shutdown() {
        em.close();
        emf.close();
    }

    public static void persist(Object entity){
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();
    }

    public static void remove(Object entity){
        em.getTransaction().begin();
        em.remove(entity);
        em.getTransaction().commit();
    }

}
