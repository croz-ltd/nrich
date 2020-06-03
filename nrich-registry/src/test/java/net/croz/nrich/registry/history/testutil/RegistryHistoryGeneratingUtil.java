package net.croz.nrich.registry.history.testutil;

import net.croz.nrich.registry.history.request.ListRegistryHistoryRequest;
import net.croz.nrich.registry.history.stub.RegistryHistoryTestEntity;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import java.util.stream.IntStream;

import static net.croz.nrich.registry.testutil.PersistenceTestUtil.executeInTransactionWithoutResult;

public final class RegistryHistoryGeneratingUtil {

    private RegistryHistoryGeneratingUtil() {
    }

    public static RegistryHistoryTestEntity creteRevisionList(final EntityManager entityManager, final PlatformTransactionManager platformTransactionManager) {
        final RegistryHistoryTestEntity entity = new RegistryHistoryTestEntity(null, "first");

        executeInTransactionWithoutResult(platformTransactionManager, () -> entityManager.persist(entity));

        IntStream.range(0, 20).forEach(value -> {
            executeInTransactionWithoutResult(platformTransactionManager, () -> {
                final RegistryHistoryTestEntity loadedEntity = entityManager.find(RegistryHistoryTestEntity.class, entity.getId());

                loadedEntity.setName("name " + value);

                entityManager.persist(loadedEntity);
            });
        });

        return entity;
    }

    public static ListRegistryHistoryRequest listRegistryHistoryRequest(final String className, final Object id) {
        final ListRegistryHistoryRequest request = new ListRegistryHistoryRequest();

        request.setRegistryId(className);
        request.setRegistryRecordId(id);
        request.setPageNumber(0);
        request.setPageSize(10);

        return request;
    }
}
