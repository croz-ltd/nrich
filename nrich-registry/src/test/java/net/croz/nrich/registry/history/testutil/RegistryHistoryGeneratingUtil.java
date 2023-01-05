/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package net.croz.nrich.registry.history.testutil;

import net.croz.nrich.registry.api.history.request.ListRegistryHistoryRequest;
import net.croz.nrich.registry.core.constants.RegistryEnversConstants;
import net.croz.nrich.registry.history.stub.RegistryHistoryTestEntity;
import net.croz.nrich.registry.history.stub.RegistryHistoryTestEntityWithEmbeddedId;
import net.croz.nrich.registry.history.stub.RegistryHistoryTestEntityWithEmbeddedObject;
import net.croz.nrich.registry.history.stub.RegistryHistoryTestEntityWithEmbeddedObjectFirstKey;
import net.croz.nrich.registry.history.stub.RegistryHistoryTestEntityWithEmbeddedObjectSecondKey;
import net.croz.nrich.search.api.model.sort.SortDirection;
import net.croz.nrich.search.api.model.sort.SortProperty;
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

    public static RegistryHistoryTestEntity creteRegistryHistoryTestEntityRevisionList(EntityManager entityManager, PlatformTransactionManager platformTransactionManager) {
        RegistryHistoryTestEntity parent = createRegistryHistoryTestEntity("parent", null, null);
        RegistryHistoryTestEntity related = createRegistryHistoryTestEntity("related", null, null);
        RegistryHistoryTestEntity entity = createRegistryHistoryTestEntity("first", parent, related);

        executeInTransactionWithoutResult(platformTransactionManager, () -> entityManager.persist(entity));

        IntStream.range(0, 20).forEach(value -> executeInTransactionWithoutResult(platformTransactionManager, () -> {
            RegistryHistoryTestEntity loadedEntity = entityManager.find(RegistryHistoryTestEntity.class, entity.getId());

            loadedEntity.setName("name " + value);

            entityManager.persist(loadedEntity);
        }));

        return entity;
    }

    public static RegistryHistoryTestEntityWithEmbeddedId creteRegistryHistoryTestEntityWithEmbeddedIdRevisionList(EntityManager entityManager, PlatformTransactionManager platformTransactionManager) {
        RegistryHistoryTestEntityWithEmbeddedId.RegistryHistoryTestEntityWithEmbeddedIdPrimaryKey id = new RegistryHistoryTestEntityWithEmbeddedId.RegistryHistoryTestEntityWithEmbeddedIdPrimaryKey();

        id.setFirstId(1L);
        id.setSecondId(1L);

        RegistryHistoryTestEntityWithEmbeddedId entity = new RegistryHistoryTestEntityWithEmbeddedId();

        entity.setId(id);
        entity.setAmount(BigDecimal.ONE);

        executeInTransactionWithoutResult(platformTransactionManager, () -> entityManager.persist(entity));

        IntStream.range(0, 20).forEach(value -> executeInTransactionWithoutResult(platformTransactionManager, () -> {
            RegistryHistoryTestEntityWithEmbeddedId loadedEntity = entityManager.find(RegistryHistoryTestEntityWithEmbeddedId.class, entity.getId());

            loadedEntity.setAmount(BigDecimal.valueOf(value));

            entityManager.persist(loadedEntity);
        }));

        return entity;
    }

    public static RegistryHistoryTestEntityWithEmbeddedObject creteRegistryHistoryTestEntityWithEmbeddedObjectIdRevisionList(EntityManager entityManager,
                                                                                                                             PlatformTransactionManager platformTransactionManager) {
        RegistryHistoryTestEntityWithEmbeddedObjectFirstKey firstKey = new RegistryHistoryTestEntityWithEmbeddedObjectFirstKey();
        RegistryHistoryTestEntityWithEmbeddedObjectSecondKey secondKey = new RegistryHistoryTestEntityWithEmbeddedObjectSecondKey();

        RegistryHistoryTestEntityWithEmbeddedObject.RegistryHistoryTestEntityWithEmbeddedObjectId id = new RegistryHistoryTestEntityWithEmbeddedObject.RegistryHistoryTestEntityWithEmbeddedObjectId();

        id.setFirstKey(firstKey);
        id.setSecondKey(secondKey);

        RegistryHistoryTestEntityWithEmbeddedObject entity = new RegistryHistoryTestEntityWithEmbeddedObject();

        entity.setId(id);
        entity.setAmount(BigDecimal.ONE);

        executeInTransactionWithoutResult(platformTransactionManager, () -> {
            entityManager.persist(firstKey);
            entityManager.persist(secondKey);
            entityManager.persist(entity);
        });

        IntStream.range(0, 20).forEach(value -> executeInTransactionWithoutResult(platformTransactionManager, () -> {
            RegistryHistoryTestEntityWithEmbeddedObject loadedEntity = entityManager.find(RegistryHistoryTestEntityWithEmbeddedObject.class, entity.getId());

            loadedEntity.setAmount(BigDecimal.valueOf(value));

            entityManager.persist(loadedEntity);
        }));

        return entity;
    }

    public static ListRegistryHistoryRequest listRegistryHistoryRequestWithSort(String className, Object id) {
        ListRegistryHistoryRequest request = listRegistryHistoryRequest(className, id);

        request.setSortPropertyList(Arrays.asList(
            new SortProperty(RegistryEnversConstants.REVISION_NUMBER_PROPERTY_NAME, SortDirection.DESC), new SortProperty(RegistryEnversConstants.REVISION_TYPE_PROPERTY_NAME, SortDirection.DESC),
            new SortProperty(RegistryEnversConstants.REVISION_TIMESTAMP_PROPERTY_NAME, SortDirection.DESC), new SortProperty("name", SortDirection.DESC),
            new SortProperty("revisionProperty", SortDirection.ASC)
        ));

        return request;
    }

    public static ListRegistryHistoryRequest listRegistryHistoryRequest(String className, Object id) {
        ListRegistryHistoryRequest request = new ListRegistryHistoryRequest();

        request.setSortPropertyList(Collections.singletonList(new SortProperty(RegistryEnversConstants.REVISION_NUMBER_PROPERTY_NAME, SortDirection.ASC)));

        request.setClassFullName(className);
        request.setRegistryRecordId(id);
        request.setPageNumber(0);
        request.setPageSize(10);

        return request;
    }

    private static RegistryHistoryTestEntity createRegistryHistoryTestEntity(String name, RegistryHistoryTestEntity parent, RegistryHistoryTestEntity related) {
        RegistryHistoryTestEntity entity = new RegistryHistoryTestEntity();

        entity.setName(name);
        entity.setParent(parent);
        entity.setRelated(related);

        return entity;
    }
}
