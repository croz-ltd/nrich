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

package net.croz.nrich.registry.history.service;

import net.croz.nrich.registry.RegistryTestConfiguration;
import net.croz.nrich.registry.api.history.model.EntityWithRevision;
import net.croz.nrich.registry.api.history.request.ListRegistryHistoryRequest;
import net.croz.nrich.registry.api.history.service.RegistryHistoryService;
import net.croz.nrich.registry.core.model.RegistryDataConfigurationHolder;
import net.croz.nrich.registry.history.stub.RegistryHistoryTestEntity;
import net.croz.nrich.registry.history.stub.RegistryHistoryTestEntityWithEmbeddedId;
import net.croz.nrich.registry.history.stub.RegistryHistoryTestEntityWithEmbeddedObject;
import org.hibernate.envers.RevisionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;

import static net.croz.nrich.registry.history.testutil.RegistryHistoryGeneratingUtil.creteRegistryHistoryTestEntityRevisionList;
import static net.croz.nrich.registry.history.testutil.RegistryHistoryGeneratingUtil.creteRegistryHistoryTestEntityWithEmbeddedIdRevisionList;
import static net.croz.nrich.registry.history.testutil.RegistryHistoryGeneratingUtil.creteRegistryHistoryTestEntityWithEmbeddedObjectIdRevisionList;
import static net.croz.nrich.registry.history.testutil.RegistryHistoryGeneratingUtil.listRegistryHistoryRequest;
import static net.croz.nrich.registry.history.testutil.RegistryHistoryGeneratingUtil.listRegistryHistoryRequestWithSort;
import static net.croz.nrich.registry.testutil.PersistenceTestUtil.executeInTransaction;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringJUnitWebConfig(RegistryTestConfiguration.class)
class DefaultRegistryHistoryServiceTest {

    @Autowired
    private RegistryHistoryService registryHistoryService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Test
    void shouldReturnAllRevisionsOfEntity() {
        // given
        RegistryHistoryTestEntity entity = creteRegistryHistoryTestEntityRevisionList(entityManager, platformTransactionManager);
        ListRegistryHistoryRequest request = listRegistryHistoryRequest(RegistryHistoryTestEntity.class.getName(), entity.getId());

        // when
        Page<EntityWithRevision<RegistryHistoryTestEntity>> resultList = registryHistoryService.historyList(request);

        // then
        assertThat(resultList).isNotEmpty();
        assertThat(resultList.getContent()).extracting("entity.name").containsExactlyInAnyOrder(
            "first", "name 0", "name 1", "name 2", "name 3", "name 4", "name 5", "name 6", "name 7", "name 8"
        );

        // and when
        EntityWithRevision<RegistryHistoryTestEntity> firstResult = resultList.getContent().get(0);

        // then
        assertThat(firstResult.getEntity().getParent()).isNotNull();
        assertThat(firstResult.getEntity().getParent().getName()).isNotNull();

        assertThat(firstResult.getRevisionInfo()).isNotNull();

        assertThat(firstResult.getRevisionInfo().getRevisionType()).isEqualTo(RevisionType.ADD.name());
        assertThat(firstResult.getRevisionInfo().getRevisionTimestamp()).isNotNull();
        assertThat(firstResult.getRevisionInfo().getRevisionNumber()).isNotNull();

        assertThat(firstResult.getRevisionInfo().getAdditionalRevisionPropertyMap()).isNotNull();
        assertThat(firstResult.getRevisionInfo().getAdditionalRevisionPropertyMap()).containsEntry("revisionProperty", "revision property value");
    }

    @Test
    void shouldReturnAllRevisionsOfEntityWithoutId() {
        // given
        creteRegistryHistoryTestEntityRevisionList(entityManager, platformTransactionManager);
        ListRegistryHistoryRequest request = listRegistryHistoryRequest(RegistryHistoryTestEntity.class.getName(), null);

        // when
        Page<EntityWithRevision<RegistryHistoryTestEntity>> resultList = registryHistoryService.historyList(request);

        // then
        assertThat(resultList).isNotEmpty();
        assertThat(resultList.getContent()).extracting("entity.name").containsExactlyInAnyOrder(
            "first", "name 0", "name 1", "name 2", "name 3", "name 4", "name 5", "name 6", "related", "parent"
        );
    }

    @Test
    void shouldReturnAllRevisionsOfEntityUnsorted() {
        // given
        creteRegistryHistoryTestEntityRevisionList(entityManager, platformTransactionManager);
        ListRegistryHistoryRequest request = listRegistryHistoryRequest(RegistryHistoryTestEntity.class.getName(), null);
        request.setSortPropertyList(null);

        // when
        Page<EntityWithRevision<RegistryHistoryTestEntity>> resultList = registryHistoryService.historyList(request);

        // then
        assertThat(resultList).isNotEmpty();
        assertThat(resultList.getContent()).extracting("entity.name").containsExactlyInAnyOrder("first", "name 0", "name 1", "name 2", "name 3", "name 4", "name 5", "name 6", "related", "parent");
    }

    @Test
    void shouldSupportSortingByAllProperties() {
        // given
        RegistryHistoryTestEntity entity = creteRegistryHistoryTestEntityRevisionList(entityManager, platformTransactionManager);
        ListRegistryHistoryRequest request = listRegistryHistoryRequestWithSort(RegistryHistoryTestEntity.class.getName(), entity.getId());

        // when
        Page<EntityWithRevision<RegistryHistoryTestEntity>> resultList = registryHistoryService.historyList(request);

        // then
        assertThat(resultList).isNotEmpty();
        assertThat(resultList.getContent().get(0).getEntity().getName()).isEqualTo("name 19");
    }

    @Test
    void shouldSupportFetchingEntityHistoryDataByEmbeddedId() {
        // given
        RegistryHistoryTestEntityWithEmbeddedId entity = creteRegistryHistoryTestEntityWithEmbeddedIdRevisionList(entityManager, platformTransactionManager);
        ListRegistryHistoryRequest request = listRegistryHistoryRequest(RegistryHistoryTestEntityWithEmbeddedId.class.getName(), entity.getId().asMap());

        // when
        Page<EntityWithRevision<RegistryHistoryTestEntityWithEmbeddedId>> resultList = registryHistoryService.historyList(request);

        // then
        assertThat(resultList).isNotEmpty();
        assertThat(resultList.getContent().get(0).getEntity().getAmount()).isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    void shouldSupportFetchingEntityHistoryDataByEmbeddedObjectId() {
        // given
        RegistryHistoryTestEntityWithEmbeddedObject entity = creteRegistryHistoryTestEntityWithEmbeddedObjectIdRevisionList(entityManager, platformTransactionManager);
        ListRegistryHistoryRequest request = listRegistryHistoryRequest(RegistryHistoryTestEntityWithEmbeddedObject.class.getName(), entity.getId().asMap());

        // when
        Page<EntityWithRevision<RegistryHistoryTestEntityWithEmbeddedObject>> resultList = registryHistoryService.historyList(request);

        // then
        assertThat(resultList).isNotEmpty();
        assertThat(resultList.getContent().get(0).getEntity().getAmount()).isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    void shouldThrowExceptionOnInvalidIdType() {
        // given
        ListRegistryHistoryRequest request = listRegistryHistoryRequest(RegistryHistoryTestEntityWithEmbeddedId.class.getName(), new Object());

        // when
        Throwable thrown = catchThrowable(() -> registryHistoryService.historyList(request));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldNotFailOnEmptyConfiguration() {
        // given
        RegistryDataConfigurationHolder registryDataConfigurationHolder = new RegistryDataConfigurationHolder(null, null);

        // when
        Throwable thrown = catchThrowable(() -> new DefaultRegistryHistoryService(null, registryDataConfigurationHolder, null, null, null));

        // then
        assertThat(thrown).isNull();
    }

    @AfterEach
    void cleanup() {
        executeInTransaction(platformTransactionManager, () -> entityManager.createQuery("delete from " + RegistryHistoryTestEntity.class.getName()).executeUpdate());
        executeInTransaction(platformTransactionManager, () -> entityManager.createQuery("delete from " + RegistryHistoryTestEntity.class.getName() + "_AUD").executeUpdate());
        executeInTransaction(platformTransactionManager, () -> entityManager.createQuery("delete from " + RegistryHistoryTestEntityWithEmbeddedId.class.getName()).executeUpdate());
        executeInTransaction(platformTransactionManager, () -> entityManager.createQuery("delete from " + RegistryHistoryTestEntityWithEmbeddedId.class.getName() + "_AUD").executeUpdate());
    }
}
