package net.croz.nrich.registry.history.testutil;

import net.croz.nrich.registry.history.request.ListRegistryHistoryRequest;
import net.croz.nrich.registry.history.stub.RegistryHistoryTestEntity;

import javax.persistence.EntityManager;

public final class RegistryHistoryGeneratingUtil {

    private RegistryHistoryGeneratingUtil() {
    }

    public static RegistryHistoryTestEntity creteRevisionList(final EntityManager entityManager) {
        final RegistryHistoryTestEntity entity = new RegistryHistoryTestEntity(null, "first");

        entityManager.persist(entity);

        entity.setName("new name");

        entityManager.persist(entity);

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
