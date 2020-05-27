package net.croz.nrich.registry.testutil;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;

public final class PersistenceTestUtil {

    private PersistenceTestUtil() {
    }

    public static void flushEntityManager(final EntityManager entityManager) {
        entityManager.flush();
        entityManager.clear();
    }

    public static void executeInTransaction(final PlatformTransactionManager transactionManager, final Runnable function) {
        new TransactionTemplate(transactionManager).execute(status -> {
            function.run();

            return null;
        });
    }
}
