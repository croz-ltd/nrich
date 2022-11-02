/*
 *  Copyright 2020-2022 CROZ d.o.o, the original author or authors.
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

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
            .collect(Collectors.toList());

        testEntityList.forEach(entityManager::persist);

        return testEntityList;
    }

    public static List<RegistryTestEntityWithDifferentIdName> createRegistryTestEntityWithDifferentIdNameList(EntityManager entityManager) {
        List<RegistryTestEntityWithDifferentIdName> testEntityList = IntStream.range(0, 5)
            .mapToObj(value -> createRegistryTestEntityWithDifferentIdName("name " + value))
            .collect(Collectors.toList());

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
        ListBulkRegistryRequest listBulkRegistryRequest = new ListBulkRegistryRequest();

        listBulkRegistryRequest.setRegistryRequestList(Collections.singletonList(createListRegistryRequest(classFullName, query)));

        return listBulkRegistryRequest;
    }

    public static ListRegistryRequest createListRegistryRequest(String classFullName, String query, int pageNumber, int pageSize) {
        ListRegistryRequest request = createListRegistryRequest(classFullName, query);

        request.setPageNumber(pageNumber);
        request.setPageSize(pageSize);

        return request;
    }

    public static List<RegistryTestEntityWithOverriddenSearchConfiguration> createRegistryTestEntityWithOverriddenSearchConfigurationList(EntityManager entityManager) {
        List<RegistryTestEntityWithOverriddenSearchConfiguration> testEntityList = IntStream.range(0, 5)
            .mapToObj(value -> createRegistryTestEntityWithOverriddenSearchConfiguration("name " + value))
            .collect(Collectors.toList());

        testEntityList.forEach(entityManager::persist);

        return testEntityList;
    }

    public static ListRegistryRequest createListRegistryRequest(String classFullName, String query) {
        SearchParameter searchParameter = null;
        if (query != null) {
            searchParameter = new SearchParameter();
            searchParameter.setPropertyNameList(Arrays.asList("age", "name"));
            searchParameter.setQuery(query);
        }

        ListRegistryRequest request = new ListRegistryRequest();

        request.setClassFullName(classFullName);
        request.setPageNumber(0);
        request.setPageSize(10);
        request.setSearchParameter(searchParameter);

        return request;
    }

    public static DeleteRegistryRequest createDeleteRegistryRequest(String classFullName, Object id) {
        DeleteRegistryRequest request = new DeleteRegistryRequest();

        request.setClassFullName(classFullName);
        request.setId(id);

        return request;
    }

    @SneakyThrows
    public static CreateRegistryRequest createRegistryRequest(ObjectMapper objectMapper, String classFullName, String name) {
        CreateRegistryRequest request = new CreateRegistryRequest();

        request.setClassFullName(classFullName);
        request.setJsonEntityData(objectMapper.writeValueAsString(createRegistryTestEntityRequest(name)));

        return request;
    }

    @SneakyThrows
    public static UpdateRegistryRequest updateRegistryRequest(ObjectMapper objectMapper, String classFullName, Long id, String name) {
        UpdateRegistryRequest request = new UpdateRegistryRequest();

        request.setClassFullName(classFullName);
        request.setId(id);
        request.setJsonEntityData(objectMapper.writeValueAsString(createRegistryTestEntityRequest(name)));

        return request;
    }

    public static RegistryTestEmbeddedUserGroup createRegistryTestEmbeddedUserGroup(EntityManager entityManager) {
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
        DeleteRegistryRequest request = new DeleteRegistryRequest();

        Map<String, Object> idMap = new HashMap<>();

        idMap.put("user", id.getUser());
        idMap.put("group", id.getGroup());

        request.setClassFullName(RegistryTestEmbeddedUserGroup.class.getName());
        request.setId(idMap);

        return request;
    }

    public static UpdateRegistryRequest createUpdateEmbeddedUserGroupRequest(ObjectMapper objectMapper, RegistryTestEmbeddedUserGroup registryTestEmbeddedUserGroup) throws Exception {
        UpdateRegistryRequest request = new UpdateRegistryRequest();

        Map<String, Object> idMap = new HashMap<>();

        idMap.put("user", registryTestEmbeddedUserGroup.getUserGroupId().getUser());
        idMap.put("group", registryTestEmbeddedUserGroup.getUserGroupId().getGroup());

        registryTestEmbeddedUserGroup.setJoinedPropertyValue("updated");

        request.setClassFullName(RegistryTestEmbeddedUserGroup.class.getName());
        request.setId(idMap);
        request.setJsonEntityData(objectMapper.writeValueAsString(registryTestEmbeddedUserGroup));

        return request;
    }

    public static Map<String, Object> registryTestEntityWithIdClassId(RegistryTestEntityWithIdClass entity) {

        Map<String, Object> idMap = new HashMap<>();

        idMap.put("firstId", entity.getFirstId());
        idMap.put("secondId", entity.getSecondId());

        return idMap;
    }

    public static CreateRegistryTestEntityRequest createRegistryTestEntityRequest(String name) {
        CreateRegistryTestEntityRequest request = new CreateRegistryTestEntityRequest();

        request.setName(name);
        request.setAge(50);

        return request;
    }

    public static UpdateRegistryTestEntityRequest createUpdateRegistryTestEntityRequest() {
        UpdateRegistryTestEntityRequest request = new UpdateRegistryTestEntityRequest();

        request.setId(100L);
        request.setName("name 2");
        request.setAge(51);

        return request;
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
