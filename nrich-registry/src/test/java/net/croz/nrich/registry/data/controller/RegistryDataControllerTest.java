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

package net.croz.nrich.registry.data.controller;

import net.croz.nrich.registry.api.data.request.ListBulkRegistryRequest;
import net.croz.nrich.registry.api.data.request.ListRegistryRequest;
import net.croz.nrich.registry.data.request.CreateRegistryRequest;
import net.croz.nrich.registry.data.request.DeleteRegistryRequest;
import net.croz.nrich.registry.data.request.UpdateRegistryRequest;
import net.croz.nrich.registry.data.stub.RegistryTestEmbeddedUserGroup;
import net.croz.nrich.registry.data.stub.RegistryTestEntity;
import net.croz.nrich.registry.test.BaseControllerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createBulkListRegistryRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createDeleteEmbeddedUserGroupRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createDeleteRegistryRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createListRegistryRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryRequestWithAssociation;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEmbeddedUserGroup;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntity;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntityList;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntityWithParent;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createUpdateEmbeddedUserGroupRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.updateRegistryRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.updateRegistryRequestWithId;
import static net.croz.nrich.registry.testutil.PersistenceTestUtil.executeInTransaction;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RegistryDataControllerTest extends BaseControllerTest {

    private static final String REGISTRY_TYPE_NAME = RegistryTestEntity.class.getName();

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Test
    void shouldBulkListRegistry() throws Exception {
        // given
        executeInTransaction(platformTransactionManager, () -> createRegistryTestEntityList(entityManager));

        String requestUrl = fullUrl("list-bulk");
        ListBulkRegistryRequest request = createBulkListRegistryRequest(REGISTRY_TYPE_NAME, "name%");

        // when
        ResultActions result = performPostRequest(requestUrl, request);

        // then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.['" + REGISTRY_TYPE_NAME + "'].content").isNotEmpty());
    }

    @Test
    void shouldListRegistry() throws Exception {
        // given
        executeInTransaction(platformTransactionManager, () -> createRegistryTestEntityList(entityManager));

        String requestUrl = fullUrl("list");
        ListRegistryRequest request = createListRegistryRequest(REGISTRY_TYPE_NAME, "name%");

        // when
        ResultActions result = performPostRequest(requestUrl, request);

        // then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.numberOfElements").value(5));
    }

    @Test
    void shouldCreateRegistryEntity() throws Exception {
        // given
        String requestUrl = fullUrl("create");
        String entityName = "name for creating";
        CreateRegistryRequest request = createRegistryRequest(objectMapper, REGISTRY_TYPE_NAME, entityName);

        // when
        ResultActions result = performPostRequest(requestUrl, request);

        // then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(entityName));
    }

    @Test
    void shouldCreateRegistryEntityWithInitializedAssociation() throws Exception {
        // given
        RegistryTestEntity entity = executeInTransaction(platformTransactionManager, () -> createRegistryTestEntity(entityManager));
        String requestUrl = fullUrl("create");
        CreateRegistryRequest request = createRegistryRequestWithAssociation(objectMapper, REGISTRY_TYPE_NAME, entity);

        // when
        ResultActions result = performPostRequest(requestUrl, request);

        // then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.parent.id").value(entity.getId()));
    }

    @Test
    void shouldReturnErrorWhenCreateInputDataIsNotValid() throws Exception {
        // given
        String requestUrl = fullUrl("create");
        CreateRegistryRequest request = createRegistryRequest(objectMapper, REGISTRY_TYPE_NAME, null);

        // when
        ResultActions result = performPostRequest(requestUrl, request);

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnErrorWhenCreateInputDataIsNotValidWithValidationGroups() throws Exception {
        // given
        String requestUrl = fullUrl("create");
        CreateRegistryRequest request = createRegistryRequest(objectMapper, REGISTRY_TYPE_NAME, "name", 10);

        // when
        ResultActions result = performPostRequest(requestUrl, request);

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateRegistryEntity() throws Exception {
        // given
        RegistryTestEntity registryTestEntity = executeInTransaction(platformTransactionManager, () -> createRegistryTestEntity(entityManager));

        String requestUrl = fullUrl("update");
        String entityName = "name for creating update";
        UpdateRegistryRequest request = updateRegistryRequest(objectMapper, REGISTRY_TYPE_NAME, registryTestEntity.getId(), entityName);

        // when
        ResultActions result = performPostRequest(requestUrl, request);

        // then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(entityName));
    }

    @Test
    void shouldUpdateRegistryEntityWithIdSentInEntityData() throws Exception {
        // given
        RegistryTestEntity registryTestEntity = executeInTransaction(platformTransactionManager, () -> createRegistryTestEntity(entityManager));

        String requestUrl = fullUrl("update");
        UpdateRegistryRequest request = updateRegistryRequestWithId(objectMapper, REGISTRY_TYPE_NAME, registryTestEntity.getId());

        // when
        ResultActions result = performPostRequest(requestUrl, request);

        // then
        result.andExpect(status().isOk());
    }

    @Test
    void shouldReturnErrorWhenUpdatingWithInvalidData() throws Exception {
        // given
        RegistryTestEntity registryTestEntity = executeInTransaction(platformTransactionManager, () -> createRegistryTestEntity(entityManager));

        String requestUrl = fullUrl("update");
        UpdateRegistryRequest request = updateRegistryRequest(objectMapper, REGISTRY_TYPE_NAME, registryTestEntity.getId(), null);

        // when
        ResultActions result = performPostRequest(requestUrl, request);

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotFailUpdatingRegistryEntityWithAssociation() throws Exception {
        // given
        RegistryTestEntity registryTestEntity = executeInTransaction(platformTransactionManager, () -> createRegistryTestEntityWithParent(entityManager));

        String requestUrl = fullUrl("update");
        String entityName = "name for update";
        UpdateRegistryRequest request = updateRegistryRequest(objectMapper, REGISTRY_TYPE_NAME, registryTestEntity.getId(), entityName);

        // when
        ResultActions result = performPostRequest(requestUrl, request);

        // then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(entityName));
    }

    @Test
    void shouldDeleteRegistryEntity() throws Exception {
        // given
        RegistryTestEntity registryTestEntity = executeInTransaction(platformTransactionManager, () -> createRegistryTestEntity(entityManager));

        String requestUrl = fullUrl("delete");
        DeleteRegistryRequest request = createDeleteRegistryRequest(RegistryTestEntity.class.getName(), registryTestEntity.getId());

        // when
        ResultActions result = performPostRequest(requestUrl, request);

        // then
        result.andExpect(status().isOk());
    }

    @Test
    void shouldDeleteRegistryEntityWithEmbeddedId() throws Exception {
        // given
        RegistryTestEmbeddedUserGroup registryTestEmbeddedUserGroup = executeInTransaction(platformTransactionManager, () -> createRegistryTestEmbeddedUserGroup(entityManager));

        String requestUrl = fullUrl("delete");
        DeleteRegistryRequest request = createDeleteEmbeddedUserGroupRequest(registryTestEmbeddedUserGroup.getUserGroupId());

        // when
        ResultActions result = performPostRequest(requestUrl, request);

        // then
        result.andExpect(status().isOk());
    }

    @Test
    void shouldNotFailListingRegistryWithLazyAssociationsInEmbeddedId() throws Exception {
        // given
        executeInTransaction(platformTransactionManager, () -> createRegistryTestEmbeddedUserGroup(entityManager));

        String requestUrl = fullUrl("list");
        ListRegistryRequest request = createListRegistryRequest(RegistryTestEmbeddedUserGroup.class.getName(), "%");

        // when
        ResultActions result = performPostRequest(requestUrl, request);

        // then
        result.andExpect(status().isOk());
    }

    @Test
    void shouldNotFailUpdatingRegistryWithLazyAssociationsInEmbeddedId() throws Exception {
        // given
        RegistryTestEmbeddedUserGroup registryTestEmbeddedUserGroup = executeInTransaction(platformTransactionManager, () -> createRegistryTestEmbeddedUserGroup(entityManager));

        String requestUrl = fullUrl("update");
        UpdateRegistryRequest request = createUpdateEmbeddedUserGroupRequest(objectMapper, registryTestEmbeddedUserGroup);

        // when
        ResultActions result = performPostRequest(requestUrl, request);

        // then
        result.andExpect(status().isOk());
    }

    @AfterEach
    void cleanup() {
        executeInTransaction(platformTransactionManager, () -> entityManager.createQuery("delete from " + RegistryTestEntity.class.getName()).executeUpdate());
    }

    private String fullUrl(String path) {
        return String.format("/nrich/registry/data/%s", path);
    }
}
