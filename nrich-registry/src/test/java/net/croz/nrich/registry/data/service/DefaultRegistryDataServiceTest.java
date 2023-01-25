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

package net.croz.nrich.registry.data.service;

import net.croz.nrich.registry.RegistryTestConfiguration;
import net.croz.nrich.registry.api.data.request.ListBulkRegistryRequest;
import net.croz.nrich.registry.api.data.request.ListRegistryRequest;
import net.croz.nrich.registry.api.data.service.RegistryDataService;
import net.croz.nrich.registry.data.stub.RegistryTestEmbeddedUserGroup;
import net.croz.nrich.registry.data.stub.RegistryTestEmbeddedUserGroupId;
import net.croz.nrich.registry.data.stub.RegistryTestEntity;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithDifferentIdName;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithEmbeddedId;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithIdClass;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithOverriddenSearchConfiguration;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithoutAssociation;
import net.croz.nrich.registry.data.stub.UpdateRegistryTestEntityRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Map;

import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createBulkListRegistryRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createListRegistryRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEmbeddedUserGroup;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEmbeddedUserGroupId;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntity;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntityList;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntityRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntityWithDifferentIdNameList;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntityWithEmbeddedId;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntityWithIdClass;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntityWithOverriddenSearchConfigurationList;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntityWithoutAssociation;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createUpdateRegistryTestEntityRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.registryTestEntityWithIdClassId;
import static net.croz.nrich.registry.data.testutil.RegistryDataResolvingUtil.findRegistryTestEmbeddedUserGroup;
import static net.croz.nrich.registry.data.testutil.RegistryDataResolvingUtil.findRegistryTestEntityWithIdClass;
import static net.croz.nrich.registry.testutil.PersistenceTestUtil.flushEntityManager;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@Transactional
@SpringJUnitWebConfig(RegistryTestConfiguration.class)
class DefaultRegistryDataServiceTest {

    @Autowired
    private RegistryDataService registryDataService;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldBulkListRegistry() {
        // given
        createRegistryTestEntityList(entityManager);

        ListBulkRegistryRequest request = createBulkListRegistryRequest(RegistryTestEntity.class.getName(), "name%");

        // when
        Map<String, Page<Object>> result = registryDataService.listBulk(request);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(RegistryTestEntity.class.getName())).hasSize(5);
    }

    @Test
    void shouldListRegistry() {
        // given
        createRegistryTestEntityList(entityManager);

        ListRegistryRequest request = createListRegistryRequest(RegistryTestEntity.class.getName(), "name%");

        // when
        Page<RegistryTestEntity> result = registryDataService.list(request);

        // then
        assertThat(result).hasSize(5);
    }

    @Test
    void shouldListRegistryEntityWithDifferentIdName() {
        // given
        createRegistryTestEntityWithDifferentIdNameList(entityManager);

        ListRegistryRequest request = createListRegistryRequest(RegistryTestEntityWithDifferentIdName.class.getName(), "name%");

        // when
        Page<RegistryTestEntity> result = registryDataService.list(request);

        // then
        assertThat(result).hasSize(5);
    }

    @Test
    void shouldListRegistryWithPaging() {
        // given
        createRegistryTestEntityList(entityManager);

        ListRegistryRequest request = createListRegistryRequest(RegistryTestEntity.class.getName(), "name%", 0, 3);

        // when
        Page<RegistryTestEntity> result = registryDataService.list(request);

        // then
        assertThat(result).hasSize(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(5);
    }

    @Test
    void shouldReturnAllElementsOnEmptySearchParameters() {
        // given
        createRegistryTestEntityList(entityManager);

        ListRegistryRequest request = createListRegistryRequest(RegistryTestEntity.class.getName(), null);

        // when
        Page<RegistryTestEntity> result = registryDataService.list(request);

        // then
        assertThat(result).hasSize(5);
    }

    @Test
    void shouldCreateRegistryEntity() {
        // when
        RegistryTestEntity registryTestEntity = registryDataService.create(RegistryTestEntity.class.getName(), createRegistryTestEntityRequest("name 1", 50));

        // then
        assertThat(registryTestEntity).isNotNull();

        // and when
        flushEntityManager(entityManager);
        RegistryTestEntity loaded = entityManager.find(RegistryTestEntity.class, registryTestEntity.getId());

        // then
        assertThat(loaded.getAge()).isEqualTo(50);
        assertThat(loaded.getName()).isEqualTo("name 1");
    }

    @Test
    void shouldUpdateRegistryEntity() {
        // given
        RegistryTestEntity registryTestEntity = createRegistryTestEntity(entityManager);
        UpdateRegistryTestEntityRequest request = createUpdateRegistryTestEntityRequest(null);

        // when
        RegistryTestEntity updatedEntity = registryDataService.update(RegistryTestEntity.class.getName(), registryTestEntity.getId(), request);

        // then
        assertThat(updatedEntity).isNotNull();

        // and when
        flushEntityManager(entityManager);
        RegistryTestEntity loaded = entityManager.find(RegistryTestEntity.class, registryTestEntity.getId());

        // then
        assertThat(loaded.getAge()).isEqualTo(51);
        assertThat(loaded.getName()).isEqualTo("name 2");
    }

    @Test
    void shouldUpdateEntityAndSkipSettingIdToOriginalValue() {
        // given
        RegistryTestEntity registryTestEntity = createRegistryTestEntity(entityManager);
        UpdateRegistryTestEntityRequest request = createUpdateRegistryTestEntityRequest(111L);

        // when
        RegistryTestEntity updatedEntity = registryDataService.update(RegistryTestEntity.class.getName(), registryTestEntity.getId(), request);

        // then
        assertThat(updatedEntity).isNotNull();

        // and when
        flushEntityManager(entityManager);
        RegistryTestEntity loaded = entityManager.find(RegistryTestEntity.class, registryTestEntity.getId());

        // then
        assertThat(loaded.getAge()).isEqualTo(51);
        assertThat(loaded.getName()).isEqualTo("name 2");
    }

    @Test
    void shouldUpdateRegistryEntityWithoutAssociations() {
        // given
        RegistryTestEntityWithoutAssociation registryTestEntity = createRegistryTestEntityWithoutAssociation(entityManager);
        UpdateRegistryTestEntityRequest request = createUpdateRegistryTestEntityRequest(registryTestEntity.getId());

        // when
        RegistryTestEntityWithoutAssociation updatedEntity = registryDataService.update(RegistryTestEntityWithoutAssociation.class.getName(), registryTestEntity.getId(), request);

        // then
        assertThat(updatedEntity).isNotNull();

        // and when
        flushEntityManager(entityManager);
        RegistryTestEntityWithoutAssociation loaded = entityManager.find(RegistryTestEntityWithoutAssociation.class, registryTestEntity.getId());

        // then
        assertThat(loaded.getAge()).isEqualTo(51);
        assertThat(loaded.getName()).isEqualTo("name 2");
    }

    @Test
    void shouldUpdateRegistryEntityWithEmbeddedObjectId() {
        // given
        String joinedPropertyUpdateValue = "updated joined property";
        RegistryTestEmbeddedUserGroup registryTestEmbeddedUserGroup = createRegistryTestEmbeddedUserGroup(entityManager);
        RegistryTestEmbeddedUserGroupId registryUpdateGroupId = createRegistryTestEmbeddedUserGroupId(entityManager);
        RegistryTestEmbeddedUserGroup entityData = createRegistryTestEmbeddedUserGroup(registryUpdateGroupId, joinedPropertyUpdateValue);

        // when
        RegistryTestEmbeddedUserGroup result = registryDataService.update(RegistryTestEmbeddedUserGroup.class.getName(), registryTestEmbeddedUserGroup.getUserGroupId(), entityData);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getJoinedPropertyValue()).isEqualTo(joinedPropertyUpdateValue);

        // and when
        flushEntityManager(entityManager);
        RegistryTestEmbeddedUserGroup loaded = findRegistryTestEmbeddedUserGroup(entityManager, registryUpdateGroupId);

        // then
        assertThat(loaded).isNotNull();
    }

    @Test
    void shouldDeleteRegistryEntity() {
        // given
        RegistryTestEntity registryTestEntity = createRegistryTestEntity(entityManager);

        // when
        RegistryTestEntity result = registryDataService.delete(RegistryTestEntity.class.getName(), registryTestEntity.getId());

        // then
        assertThat(result.getId()).isEqualTo(registryTestEntity.getId());
    }

    @Test
    void shouldDeleteRegistryEntityWithEmbeddedObjectId() {
        // given
        RegistryTestEmbeddedUserGroup registryTestEmbeddedUserGroup = createRegistryTestEmbeddedUserGroup(entityManager);

        // when
        RegistryTestEmbeddedUserGroup result = registryDataService.delete(RegistryTestEmbeddedUserGroup.class.getName(), registryTestEmbeddedUserGroup.getUserGroupId());

        // then
        assertThat(result).isNotNull();

        // and when
        flushEntityManager(entityManager);
        RegistryTestEmbeddedUserGroup loaded = findRegistryTestEmbeddedUserGroup(entityManager, registryTestEmbeddedUserGroup.getUserGroupId());

        // then
        assertThat(loaded).isNull();
    }

    @Test
    void shouldDeleteRegistryEntityWithEmbeddedId() {
        // given
        RegistryTestEntityWithEmbeddedId registryTestEntityWithEmbeddedId = createRegistryTestEntityWithEmbeddedId(entityManager);

        // when
        RegistryTestEntityWithEmbeddedId result = registryDataService.delete(RegistryTestEntityWithEmbeddedId.class.getName(), registryTestEntityWithEmbeddedId.getId().asMap());

        // then
        assertThat(result).isNotNull();

        // and when
        flushEntityManager(entityManager);
        RegistryTestEntityWithEmbeddedId loaded = entityManager.find(RegistryTestEntityWithEmbeddedId.class, registryTestEntityWithEmbeddedId.getId());

        // then
        assertThat(loaded).isNull();
    }

    @Test
    void shouldThrowExceptionOnInvalidPrimaryKey() {
        // given
        createRegistryTestEmbeddedUserGroup(entityManager);

        // when
        Throwable thrown = catchThrowable(() -> registryDataService.delete(RegistryTestEmbeddedUserGroup.class.getName(), new Object()));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldListRegistryWithOverriddenSearchConfiguration() {
        // given
        createRegistryTestEntityWithOverriddenSearchConfigurationList(entityManager);

        ListRegistryRequest request = createListRegistryRequest(RegistryTestEntityWithOverriddenSearchConfiguration.class.getName(), "name%");

        // when
        Page<RegistryTestEntity> result = registryDataService.list(request);

        // then
        assertThat(result).isEmpty();

        // and when
        ListRegistryRequest requestWithEqualName = createListRegistryRequest(RegistryTestEntityWithOverriddenSearchConfiguration.class.getName(), "name 0");

        Page<RegistryTestEntity> resultWithEqualName = registryDataService.list(requestWithEqualName);

        // then
        assertThat(resultWithEqualName).hasSize(1);
    }

    @Test
    void shouldThrowExceptionWhenConfigurationHasNotBeenDefined() {
        // given
        ListRegistryRequest request = createListRegistryRequest("undefined.configuration", "name%");

        // when
        Throwable thrown = catchThrowable(() -> registryDataService.list(request));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldDeleteRegistryEntityWithIdClassId() {
        // given
        RegistryTestEntityWithIdClass registryTestEntityWithIdClass = createRegistryTestEntityWithIdClass(entityManager);

        // when
        RegistryTestEntityWithIdClass result = registryDataService.delete(RegistryTestEntityWithIdClass.class.getName(), registryTestEntityWithIdClassId(registryTestEntityWithIdClass));

        // then
        assertThat(result).isNotNull();

        // and when
        flushEntityManager(entityManager);
        RegistryTestEntityWithIdClass loaded = findRegistryTestEntityWithIdClass(entityManager, registryTestEntityWithIdClass);

        // then
        assertThat(loaded).isNull();
    }

    @Test
    void shouldCreateRegistryEntityWithAssociationFromRequestClass() {
        // given
        RegistryTestEntity registryTestEntity = createRegistryTestEntity(entityManager);

        // when
        RegistryTestEntity createdTestEntity = registryDataService.create(RegistryTestEntity.class.getName(), createRegistryTestEntityRequest("name 1", registryTestEntity.getId()));

        // then
        assertThat(createdTestEntity).isNotNull();

        // and when
        flushEntityManager(entityManager);
        RegistryTestEntity loaded = entityManager.find(RegistryTestEntity.class, createdTestEntity.getId());

        // then
        assertThat(loaded.getAge()).isEqualTo(50);
        assertThat(loaded.getName()).isEqualTo("name 1");
        assertThat(loaded.getParent()).isNotNull();
    }
}
