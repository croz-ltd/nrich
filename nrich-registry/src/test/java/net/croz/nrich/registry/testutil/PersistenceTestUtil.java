package net.croz.nrich.registry.testutil;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public final class PersistenceTestUtil {

    private PersistenceTestUtil() {
    }

    public static void flushEntityManager(final EntityManager entityManager) {
        entityManager.flush();
        entityManager.clear();
    }

    @SuppressWarnings("unchecked")
    public static <T> T executeInTransaction(final PlatformTransactionManager transactionManager, final Supplier<?> function) {
        return (T) new TransactionTemplate(transactionManager).execute(status -> function.get());
    }

    public static void executeInTransactionWithoutResult(final PlatformTransactionManager transactionManager, final Runnable function) {
        new TransactionTemplate(transactionManager).execute(status -> {
            function.run();

            return null;
        });
    }
}
