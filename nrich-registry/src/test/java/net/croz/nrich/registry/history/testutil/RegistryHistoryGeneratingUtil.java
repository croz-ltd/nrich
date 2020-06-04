package net.croz.nrich.registry.history.testutil;

import net.croz.nrich.registry.history.constants.RegistryHistoryConstants;
import net.croz.nrich.registry.history.request.ListRegistryHistoryRequest;
import net.croz.nrich.registry.history.stub.RegistryHistoryTestEntity;
import net.croz.nrich.registry.history.stub.RegistryHistoryTestEntityWithEmbeddedId;
import net.croz.nrich.search.api.model.SortDirection;
import net.croz.nrich.search.api.model.SortProperty;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.IntStream;

import static net.croz.nrich.registry.testutil.PersistenceTestUtil.executeInTransactionWithoutResult;

public final class RegistryHistoryGeneratingUtil {

    private RegistryHistoryGeneratingUtil() {
    }

    public static RegistryHistoryTestEntity creteRegistryHistoryTestEntityRevisionList(final EntityManager entityManager, final PlatformTransactionManager platformTransactionManager) {
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

    public static RegistryHistoryTestEntityWithEmbeddedId creteRegistryHistoryTestEntityWithEmbeddedIdRevisionList(final EntityManager entityManager, final PlatformTransactionManager platformTransactionManager) {
        final RegistryHistoryTestEntityWithEmbeddedId.RegistryConfigurationTestEntityPrimaryKey primaryKey = new RegistryHistoryTestEntityWithEmbeddedId.RegistryConfigurationTestEntityPrimaryKey(1L, 1L);
        final RegistryHistoryTestEntityWithEmbeddedId entity = new RegistryHistoryTestEntityWithEmbeddedId(primaryKey, BigDecimal.ONE);

        executeInTransactionWithoutResult(platformTransactionManager, () -> entityManager.persist(entity));

        IntStream.range(0, 20).forEach(value -> {
            executeInTransactionWithoutResult(platformTransactionManager, () -> {
                final RegistryHistoryTestEntityWithEmbeddedId loadedEntity = entityManager.find(RegistryHistoryTestEntityWithEmbeddedId.class, entity.getId());

                loadedEntity.setAmount(BigDecimal.valueOf(value));

                entityManager.persist(loadedEntity);
            });
        });

        return entity;
    }

    public static ListRegistryHistoryRequest listRegistryHistoryRequestWithSort(final String className, final Object id) {
        final ListRegistryHistoryRequest request = listRegistryHistoryRequest(className, id);

        request.setSortPropertyList(Arrays.asList(
                new SortProperty(RegistryHistoryConstants.REVISION_NUMBER_PROPERTY, SortDirection.DESC), new SortProperty(RegistryHistoryConstants.REVISION_TYPE_PROPERTY, SortDirection.DESC),
                new SortProperty("name", SortDirection.DESC)
        ));

        return request;
    }

    public static ListRegistryHistoryRequest listRegistryHistoryRequest(final String className, final Object id) {
        final ListRegistryHistoryRequest request = new ListRegistryHistoryRequest();

        request.setSortPropertyList(Collections.singletonList(new SortProperty(RegistryHistoryConstants.REVISION_NUMBER_PROPERTY, SortDirection.ASC)));

        request.setRegistryId(className);
        request.setRegistryRecordId(id);
        request.setPageNumber(0);
        request.setPageSize(10);

        return request;
    }
}
