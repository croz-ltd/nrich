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

package net.croz.nrich.registry.data.testutil;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import net.croz.nrich.registry.api.data.request.ListBulkRegistryRequest;
import net.croz.nrich.registry.api.data.request.ListRegistryRequest;
import net.croz.nrich.registry.api.data.request.SearchParameter;
import net.croz.nrich.registry.data.request.CreateRegistryRequest;
import net.croz.nrich.registry.data.request.DeleteRegistryRequest;
import net.croz.nrich.registry.data.request.UpdateRegistryRequest;
import net.croz.nrich.registry.data.stub.CreateRegistryTestEntityRequest;
import net.croz.nrich.registry.data.stub.RegistryTestEmbeddedGroup;
import net.croz.nrich.registry.data.stub.RegistryTestEmbeddedUser;
import net.croz.nrich.registry.data.stub.RegistryTestEmbeddedUserGroup;
import net.croz.nrich.registry.data.stub.RegistryTestEmbeddedUserGroupId;
import net.croz.nrich.registry.data.stub.RegistryTestEntity;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithDifferentIdName;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithEmbeddedId;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithIdClass;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithOverriddenSearchConfiguration;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithoutAssociation;
import net.croz.nrich.registry.data.stub.RegistryTestGroupType;
import net.croz.nrich.registry.data.stub.UpdateRegistryTestEntityRequest;
import net.croz.nrich.registry.data.stub.request.CreateEntityRequest;

import jakarta.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public final class RegistryDataGeneratingUtil {

    private RegistryDataGeneratingUtil() {
    }

    public static RegistryTestEntity createRegistryTestEntity(EntityManager entityManager) {
        RegistryTestEntity registryTestEntity = createRegistryTestEntity("name 1", 50, null);

        entityManager.persist(registryTestEntity);

        return registryTestEntity;
    }

    public static RegistryTestEntity createRegistryTestEntityWithParent(EntityManager entityManager) {
        RegistryTestEntity parentEntity = createRegistryTestEntity("parent", 60, null);

        entityManager.persist(parentEntity);

        RegistryTestEntity registryTestEntity = createRegistryTestEntity("name 1", 50, parentEntity);

        entityManager.persist(registryTestEntity);

        return registryTestEntity;
    }

    public static RegistryTestEntityWithoutAssociation createRegistryTestEntityWithoutAssociation(EntityManager entityManager) {
        RegistryTestEntityWithoutAssociation registryTestEntity = new RegistryTestEntityWithoutAssociation();

        registryTestEntity.setName("name 1");
        registryTestEntity.setAge(50);

        entityManager.persist(registryTestEntity);

        return registryTestEntity;
    }

    public static List<RegistryTestEntity> createRegistryTestEntityList(EntityManager entityManager) {
        List<RegistryTestEntity> testEntityList = IntStream.range(0, 5)
            .mapToObj(value -> createRegistryTestEntity("name " + value, 50 + value, null))
            .toList();

        testEntityList.forEach(entityManager::persist);

        return testEntityList;
    }

    public static List<RegistryTestEntityWithDifferentIdName> createRegistryTestEntityWithDifferentIdNameList(EntityManager entityManager) {
        List<RegistryTestEntityWithDifferentIdName> testEntityList = IntStream.range(0, 5)
            .mapToObj(value -> createRegistryTestEntityWithDifferentIdName("name " + value))
            .toList();

        testEntityList.forEach(entityManager::persist);

        return testEntityList;
    }

    public static RegistryTestEntityWithEmbeddedId createRegistryTestEntityWithEmbeddedId(EntityManager entityManager) {
        RegistryTestEntityWithEmbeddedId.RegistryTestEntityWithEmbeddedIdPrimaryKey primaryKey = new RegistryTestEntityWithEmbeddedId.RegistryTestEntityWithEmbeddedIdPrimaryKey();

        primaryKey.setFirstId(1L);
        primaryKey.setSecondId(2L);

        RegistryTestEntityWithEmbeddedId registryTestEntity = new RegistryTestEntityWithEmbeddedId();

        registryTestEntity.setId(primaryKey);
        registryTestEntity.setName("name 1");

        entityManager.persist(registryTestEntity);

        return registryTestEntity;
    }

    public static ListBulkRegistryRequest createBulkListRegistryRequest(String classFullName, String query) {
        return new ListBulkRegistryRequest(List.of(createListRegistryRequest(classFullName, query)));
    }

    public static ListRegistryRequest createListRegistryRequest(String classFullName, String query) {
        return createListRegistryRequest(classFullName, query, 0, 10);
    }

    public static List<RegistryTestEntityWithOverriddenSearchConfiguration> createRegistryTestEntityWithOverriddenSearchConfigurationList(EntityManager entityManager) {
        List<RegistryTestEntityWithOverriddenSearchConfiguration> testEntityList = IntStream.range(0, 5)
            .mapToObj(value -> createRegistryTestEntityWithOverriddenSearchConfiguration("name " + value))
            .toList();

        testEntityList.forEach(entityManager::persist);

        return testEntityList;
    }

    public static ListRegistryRequest createListRegistryRequest(String classFullName, String query, int pageNumber, int pageSize) {
        SearchParameter searchParameter = null;
        if (query != null) {
            searchParameter = new SearchParameter(List.of("age", "name"), query);
        }

        return new ListRegistryRequest(classFullName, pageNumber, pageSize, searchParameter, Collections.emptyList());
    }

    public static DeleteRegistryRequest createDeleteRegistryRequest(String classFullName, Object id) {
        return new DeleteRegistryRequest(classFullName, id);
    }

    @SneakyThrows
    public static CreateRegistryRequest createRegistryRequest(ObjectMapper objectMapper, String classFullName, String name) {
        return createRegistryRequest(objectMapper, classFullName, name, 50);
    }

    @SneakyThrows
    public static CreateRegistryRequest createRegistryRequestWithAssociation(ObjectMapper objectMapper, String classFullName, RegistryTestEntity parent) {
        String jsonEntityData = objectMapper.writeValueAsString(Map.of("name", "name", "age", 40, "parent", parent));

        return new CreateRegistryRequest(classFullName, jsonEntityData);
    }

    @SneakyThrows
    public static CreateRegistryRequest createRegistryRequest(ObjectMapper objectMapper, String classFullName, String name, Integer age) {
        String jsonData = objectMapper.writeValueAsString(createRegistryTestEntityRequest(name, age));

        return new CreateRegistryRequest(classFullName, jsonData);
    }

    @SneakyThrows
    public static UpdateRegistryRequest updateRegistryRequest(ObjectMapper objectMapper, String classFullName, Long id, String name) {
        String jsonData = objectMapper.writeValueAsString(createRegistryTestEntityRequest(name, 50));

        return new UpdateRegistryRequest(classFullName, id, jsonData);
    }

    @SneakyThrows
    public static UpdateRegistryRequest updateRegistryRequest(ObjectMapper objectMapper, String classFullName, Long id, Long parentId) {
        String jsonData = objectMapper.writeValueAsString(createRegistryTestEntityRequest("name", parentId));

        return new UpdateRegistryRequest(classFullName, id, jsonData);
    }

    @SneakyThrows
    public static UpdateRegistryRequest updateRegistryRequestWithId(ObjectMapper objectMapper, String classFullName, Long id) {
        String jsonData = objectMapper.writeValueAsString(createUpdateRegistryTestEntityRequest(id));

        return new UpdateRegistryRequest(classFullName, id, jsonData);
    }

    public static RegistryTestEmbeddedUserGroup createAndSaveRegistryTestEmbeddedUserGroup(EntityManager entityManager) {
        RegistryTestEmbeddedUser user = createRegistryTestEmbeddedUser(entityManager);
        RegistryTestEmbeddedGroup group = createRegistryTestEmbeddedGroup(entityManager);

        RegistryTestEmbeddedUserGroupId userGroupId = new RegistryTestEmbeddedUserGroupId();

        userGroupId.setUser(user);
        userGroupId.setGroup(group);

        RegistryTestEmbeddedUserGroup userGroup = new RegistryTestEmbeddedUserGroup();

        userGroup.setUserGroupId(userGroupId);
        userGroup.setJoinedPropertyValue("value");

        entityManager.persist(userGroup);

        return userGroup;
    }

    public static RegistryTestEmbeddedUserGroup createRegistryTestEmbeddedUserGroup(RegistryTestEmbeddedUserGroupId registryUpdateGroupId, String joinedPropertyValue) {
        RegistryTestEmbeddedUserGroup entity = new RegistryTestEmbeddedUserGroup();

        entity.setUserGroupId(registryUpdateGroupId);
        entity.setJoinedPropertyValue(joinedPropertyValue);

        return entity;
    }

    public static RegistryTestEmbeddedUserGroupId createRegistryTestEmbeddedUserGroupId(EntityManager entityManager) {
        RegistryTestEmbeddedUser user = createRegistryTestEmbeddedUser(entityManager);
        RegistryTestEmbeddedGroup group = createRegistryTestEmbeddedGroup(entityManager);

        RegistryTestEmbeddedUserGroupId entity = new RegistryTestEmbeddedUserGroupId();

        entity.setUser(user);
        entity.setGroup(group);

        return entity;
    }

    public static RegistryTestEntityWithIdClass createRegistryTestEntityWithIdClass(EntityManager entityManager) {
        RegistryTestEntityWithIdClass registryTestEntityWithIdClass = new RegistryTestEntityWithIdClass();

        registryTestEntityWithIdClass.setFirstId(1L);
        registryTestEntityWithIdClass.setSecondId(2L);
        registryTestEntityWithIdClass.setName("value");

        entityManager.persist(registryTestEntityWithIdClass);

        return registryTestEntityWithIdClass;
    }

    public static DeleteRegistryRequest createDeleteEmbeddedUserGroupRequest(RegistryTestEmbeddedUserGroupId id) {
        Map<String, Object> idMap = Map.of("user", id.getUser(), "group", id.getGroup());

        return new DeleteRegistryRequest(RegistryTestEmbeddedUserGroup.class.getName(), idMap);
    }

    public static UpdateRegistryRequest createUpdateEmbeddedUserGroupRequest(ObjectMapper objectMapper, RegistryTestEmbeddedUserGroup registryTestEmbeddedUserGroup) throws Exception {
        Map<String, Object> idMap = Map.of(
            "user", registryTestEmbeddedUserGroup.getUserGroupId().getUser(),
            "group", registryTestEmbeddedUserGroup.getUserGroupId().getGroup()
        );

        registryTestEmbeddedUserGroup.setJoinedPropertyValue("updated");

        String jsonData = objectMapper.writeValueAsString(registryTestEmbeddedUserGroup);

        return new UpdateRegistryRequest(RegistryTestEmbeddedUserGroup.class.getName(), idMap, jsonData);
    }

    public static Map<String, Object> registryTestEntityWithIdClassId(RegistryTestEntityWithIdClass entity) {
        return Map.of("firstId", entity.getFirstId(), "secondId", entity.getSecondId());
    }

    public static CreateRegistryTestEntityRequest createRegistryTestEntityRequest(String name, Long parentId) {
        CreateEntityRequest parent = new CreateEntityRequest();

        parent.setId(parentId);

        CreateRegistryTestEntityRequest request = createRegistryTestEntityRequest(name, 50);

        request.setParent(parent);

        return request;
    }

    public static CreateRegistryTestEntityRequest createRegistryTestEntityRequest(String name, Integer age) {
        CreateRegistryTestEntityRequest request = new CreateRegistryTestEntityRequest();

        request.setName(name);
        request.setAge(age);

        return request;
    }

    public static UpdateRegistryTestEntityRequest createUpdateRegistryTestEntityRequest(Long id) {
        UpdateRegistryTestEntityRequest request = new UpdateRegistryTestEntityRequest();

        request.setId(id);
        request.setName("name 2");
        request.setAge(51);

        return request;
    }

    public static ListRegistryRequest createEmbeddedIdSearchRequest(RegistryTestEmbeddedUserGroup entity) {
        SearchParameter searchParameter = new SearchParameter(List.of("userGroupId.user"), entity.getUserGroupId().getGroup().getId().toString());

        return new ListRegistryRequest(RegistryTestEmbeddedUserGroup.class.getName(), 0, 10, searchParameter, Collections.emptyList());
    }

    private static RegistryTestEntity createRegistryTestEntity(String name, Integer age, RegistryTestEntity parent) {
        RegistryTestEntity entity = new RegistryTestEntity();

        entity.setName(name);
        entity.setAge(age);
        entity.setParent(parent);

        return entity;
    }

    private static RegistryTestEntityWithDifferentIdName createRegistryTestEntityWithDifferentIdName(String name) {
        RegistryTestEntityWithDifferentIdName entity = new RegistryTestEntityWithDifferentIdName();

        entity.setName(name);

        return entity;
    }

    private static RegistryTestEntityWithOverriddenSearchConfiguration createRegistryTestEntityWithOverriddenSearchConfiguration(String name) {
        RegistryTestEntityWithOverriddenSearchConfiguration entity = new RegistryTestEntityWithOverriddenSearchConfiguration();

        entity.setName(name);

        return entity;
    }

    private static RegistryTestEmbeddedUser createRegistryTestEmbeddedUser(EntityManager entityManager) {
        RegistryTestEmbeddedUser entity = new RegistryTestEmbeddedUser();

        entity.setName("user name");

        entityManager.persist(entity);

        return entity;
    }

    private static RegistryTestEmbeddedGroup createRegistryTestEmbeddedGroup(EntityManager entityManager) {
        RegistryTestEmbeddedGroup entity = new RegistryTestEmbeddedGroup();

        entity.setName("user group");
        entity.setGroupType(createRegistryTestGroupType(entityManager));

        entityManager.persist(entity);

        return entity;
    }

    private static RegistryTestGroupType createRegistryTestGroupType(EntityManager entityManager) {
        RegistryTestGroupType entity = new RegistryTestGroupType();

        entity.setName("group type");

        entityManager.persist(entity);

        return entity;
    }
}
