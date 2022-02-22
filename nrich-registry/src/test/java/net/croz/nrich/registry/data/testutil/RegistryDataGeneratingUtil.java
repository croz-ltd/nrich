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
        RegistryTestEntity registryTestEntity = new RegistryTestEntity(null, "name 1", 50, null);

        entityManager.persist(registryTestEntity);

        return registryTestEntity;
    }

    public static RegistryTestEntity createRegistryTestEntityWithParent(EntityManager entityManager) {
        RegistryTestEntity parentEntity = new RegistryTestEntity(null, "parent", 60, null);

        entityManager.persist(parentEntity);

        RegistryTestEntity registryTestEntity = new RegistryTestEntity(null, "name 1", 50, parentEntity);

        entityManager.persist(registryTestEntity);

        return registryTestEntity;
    }

    public static RegistryTestEntityWithoutAssociation createRegistryTestEntityWithoutAssociation(EntityManager entityManager) {
        RegistryTestEntityWithoutAssociation registryTestEntity = new RegistryTestEntityWithoutAssociation(null, "name 1", 50);

        entityManager.persist(registryTestEntity);

        return registryTestEntity;
    }

    public static List<RegistryTestEntity> createRegistryTestEntityList(EntityManager entityManager) {
        List<RegistryTestEntity> testEntityList = IntStream.range(0, 5).mapToObj(value -> new RegistryTestEntity(null, "name " + value, 50 + value, null)).collect(Collectors.toList());

        testEntityList.forEach(entityManager::persist);

        return testEntityList;
    }

    public static List<RegistryTestEntityWithDifferentIdName> createRegistryTestEntityWithDifferentIdNameList(EntityManager entityManager) {
        List<RegistryTestEntityWithDifferentIdName> testEntityList = IntStream.range(0, 5).mapToObj(value -> new RegistryTestEntityWithDifferentIdName(null, "name " + value)).collect(Collectors.toList());

        testEntityList.forEach(entityManager::persist);

        return testEntityList;
    }

    public static RegistryTestEntityWithEmbeddedId createRegistryTestEntityWithEmbeddedId(EntityManager entityManager) {
        RegistryTestEntityWithEmbeddedId.RegistryTestEntityWithEmbeddedIdPrimaryKey primaryKey = new RegistryTestEntityWithEmbeddedId.RegistryTestEntityWithEmbeddedIdPrimaryKey(1L, 2L);
        RegistryTestEntityWithEmbeddedId registryTestEntity = new RegistryTestEntityWithEmbeddedId(primaryKey, "name 1");

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
        List<RegistryTestEntityWithOverriddenSearchConfiguration> testEntityList = IntStream.range(0, 5).mapToObj(value -> new RegistryTestEntityWithOverriddenSearchConfiguration(null, "name " + value)).collect(Collectors.toList());

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
        request.setJsonEntityData(objectMapper.writeValueAsString(new CreateRegistryTestEntityRequest(name, 50)));

        return request;
    }

    @SneakyThrows
    public static UpdateRegistryRequest updateRegistryRequest(ObjectMapper objectMapper, String classFullName, Long id, String name) {
        UpdateRegistryRequest request = new UpdateRegistryRequest();

        request.setClassFullName(classFullName);
        request.setId(id);
        request.setJsonEntityData(objectMapper.writeValueAsString(new CreateRegistryTestEntityRequest(name, 50)));

        return request;
    }

    public static RegistryTestEmbeddedUserGroup createRegistryTestEmbeddedUserGroup(EntityManager entityManager) {
        RegistryTestEmbeddedUser user = new RegistryTestEmbeddedUser(null, "user name");
        RegistryTestEmbeddedGroup group = new RegistryTestEmbeddedGroup(null, "user group");

        entityManager.persist(user);
        entityManager.persist(group);

        RegistryTestEmbeddedUserGroupId groupId = new RegistryTestEmbeddedUserGroupId(user, group);

        RegistryTestEmbeddedUserGroup userGroup = new RegistryTestEmbeddedUserGroup(groupId, "value");

        entityManager.persist(userGroup);

        return userGroup;
    }

    public static RegistryTestEmbeddedUserGroupId createRegistryTestEmbeddedUserGroupId(EntityManager entityManager) {
        RegistryTestEmbeddedUser user = new RegistryTestEmbeddedUser(null, "user name");
        RegistryTestEmbeddedGroup group = new RegistryTestEmbeddedGroup(null, "user group");

        entityManager.persist(user);
        entityManager.persist(group);

        return new RegistryTestEmbeddedUserGroupId(user, group);
    }

    public static RegistryTestEntityWithIdClass createRegistryTestEntityWithIdClass(EntityManager entityManager) {
        RegistryTestEntityWithIdClass registryTestEntityWithIdClass = new RegistryTestEntityWithIdClass(1L, 2L, "value");

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

    public static Map<String, Object> registryTestEntityWithIdClassId(RegistryTestEntityWithIdClass entity) {

        Map<String, Object> idMap = new HashMap<>();

        idMap.put("firstId", entity.getFirstId());
        idMap.put("secondId", entity.getSecondId());

        return idMap;
    }

}
