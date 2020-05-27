package net.croz.nrich.registry.testutil;

import javax.persistence.EntityManager;

public final class EntityManagerTestUtil {

    private EntityManagerTestUtil() {
    }

    public static void flushEntityManager(EntityManager entityManager) {
        entityManager.flush();
        entityManager.clear();
    }

}
