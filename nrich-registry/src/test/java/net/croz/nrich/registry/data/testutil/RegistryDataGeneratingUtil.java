package net.croz.nrich.registry.data.testutil;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import net.croz.nrich.registry.data.request.CreateRegistryRequest;
import net.croz.nrich.registry.data.request.CreateRegistryServiceRequest;
import net.croz.nrich.registry.data.request.DeleteRegistryRequest;
import net.croz.nrich.registry.data.request.ListBulkRegistryRequest;
import net.croz.nrich.registry.data.request.ListRegistryRequest;
import net.croz.nrich.registry.data.request.SearchParameter;
import net.croz.nrich.registry.data.request.UpdateRegistryRequest;
import net.croz.nrich.registry.data.request.UpdateRegistryServiceRequest;
import net.croz.nrich.registry.data.stub.CreateRegistryTestEntityRequest;
import net.croz.nrich.registry.data.stub.RegistryTestEmbeddedGroup;
import net.croz.nrich.registry.data.stub.RegistryTestEmbeddedUser;
import net.croz.nrich.registry.data.stub.RegistryTestEmbeddedUserGroup;
import net.croz.nrich.registry.data.stub.RegistryTestEmbeddedUserGroupId;
import net.croz.nrich.registry.data.stub.RegistryTestEntity;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithEmbeddedId;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithOverriddenSearchConfiguration;
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

    public static RegistryTestEntity createRegistryTestEntity(final EntityManager entityManager) {
        final RegistryTestEntity registryTestEntity = new RegistryTestEntity(null, "name 1", 50);

        entityManager.persist(registryTestEntity);

        return registryTestEntity;
    }

    public static List<RegistryTestEntity> createRegistryTestEntityList(final EntityManager entityManager) {
        final List<RegistryTestEntity> testEntityList = IntStream.range(0, 5).mapToObj(value -> new RegistryTestEntity(null, "name " + value, 50 + value)).collect(Collectors.toList());

        testEntityList.forEach(entityManager::persist);

        return testEntityList;
    }

    public static RegistryTestEntityWithEmbeddedId createRegistryTestEntityWithEmbeddedId(final EntityManager entityManager) {
        final RegistryTestEntityWithEmbeddedId.RegistryTestEntityWithEmbeddedIdPrimaryKey primaryKey = new RegistryTestEntityWithEmbeddedId.RegistryTestEntityWithEmbeddedIdPrimaryKey(1L, 2L);
        final RegistryTestEntityWithEmbeddedId registryTestEntity = new RegistryTestEntityWithEmbeddedId(primaryKey, "name 1");

        entityManager.persist(registryTestEntity);

        return registryTestEntity;
    }

    public static ListBulkRegistryRequest createBulkListRegistryRequest(final String classFullName, final String query) {
        final ListBulkRegistryRequest listBulkRegistryRequest = new ListBulkRegistryRequest();

        listBulkRegistryRequest.setRegistryRequestList(Collections.singletonList(createListRegistryRequest(classFullName, query)));

        return listBulkRegistryRequest;
    }

    public static ListRegistryRequest createListRegistryRequest(final String classFullName, final String query, final int pageNumber, final int pageSize) {
        final ListRegistryRequest request = createListRegistryRequest(classFullName, query);

        request.setPageNumber(pageNumber);
        request.setPageSize(pageSize);

        return request;
    }

    public static List<RegistryTestEntityWithOverriddenSearchConfiguration> createRegistryTestEntityWithOverriddenSearchConfigurationList(final EntityManager entityManager) {
        final List<RegistryTestEntityWithOverriddenSearchConfiguration> testEntityList = IntStream.range(0, 5).mapToObj(value -> new RegistryTestEntityWithOverriddenSearchConfiguration(null, "name " + value)).collect(Collectors.toList());

        testEntityList.forEach(entityManager::persist);

        return testEntityList;
    }

    public static ListRegistryRequest createListRegistryRequest(final String classFullName, final String query) {
        SearchParameter searchParameter = null;
        if (query != null) {
            searchParameter = new SearchParameter();
            searchParameter.setPropertyNameList(Arrays.asList("age", "name"));
            searchParameter.setQuery(query);
        }

        final ListRegistryRequest request = new ListRegistryRequest();

        request.setRegistryId(classFullName);
        request.setPageNumber(0);
        request.setPageSize(10);
        request.setSearchParameter(searchParameter);

        return request;
    }

    public static DeleteRegistryRequest createDeleteRegistryRequest(final String classFullName, final Object id) {
        final DeleteRegistryRequest request = new DeleteRegistryRequest();

        request.setClassFullName(classFullName);
        request.setId(id);

        return request;
    }

    @SneakyThrows
    public static CreateRegistryRequest createRegistryRequest(final ObjectMapper objectMapper, final String classFullName, final String name) {
        final CreateRegistryRequest request = new CreateRegistryRequest();

        request.setClassFullName(classFullName);
        request.setEntityData(objectMapper.writeValueAsString(new CreateRegistryTestEntityRequest(name, 50)));

        return request;
    }

    public static CreateRegistryServiceRequest createRegistryServiceRequest(final String classFullName) {
        final CreateRegistryServiceRequest request = new CreateRegistryServiceRequest();

        request.setClassFullName(classFullName);
        request.setEntityData(new CreateRegistryTestEntityRequest("name 1", 50));

        return request;
    }

    public static UpdateRegistryServiceRequest updateRegistryServiceRequest(final String classFullName, final Long id) {
        final UpdateRegistryServiceRequest request = new UpdateRegistryServiceRequest();

        request.setClassFullName(classFullName);
        request.setId(id);
        request.setEntityData(new UpdateRegistryTestEntityRequest(100L, "name 2", 51));

        return request;
    }

    @SneakyThrows
    public static UpdateRegistryRequest updateRegistryRequest(final ObjectMapper objectMapper, final String classFullName, final Long id, final String name) {
        final UpdateRegistryRequest request = new UpdateRegistryRequest();

        request.setClassFullName(classFullName);
        request.setId(id);
        request.setEntityData(objectMapper.writeValueAsString(new CreateRegistryTestEntityRequest(name, 50)));

        return request;
    }

    public static RegistryTestEmbeddedUserGroup createRegistryTestEmbeddedUserGroup(final EntityManager entityManager) {
        final RegistryTestEmbeddedUser user = new RegistryTestEmbeddedUser(null, "user name");
        final RegistryTestEmbeddedGroup group = new RegistryTestEmbeddedGroup(null, "user group");

        entityManager.persist(user);
        entityManager.persist(group);

        final RegistryTestEmbeddedUserGroupId groupId = new RegistryTestEmbeddedUserGroupId(user, group);

        final RegistryTestEmbeddedUserGroup userGroup = new RegistryTestEmbeddedUserGroup(groupId, "value");

        entityManager.persist(userGroup);

        return userGroup;
    }

    public static RegistryTestEmbeddedUserGroupId createRegistryTestEmbeddedUserGroupId(final EntityManager entityManager) {
        final RegistryTestEmbeddedUser user = new RegistryTestEmbeddedUser(null, "user name");
        final RegistryTestEmbeddedGroup group = new RegistryTestEmbeddedGroup(null, "user group");

        entityManager.persist(user);
        entityManager.persist(group);

        return new RegistryTestEmbeddedUserGroupId(user, group);
    }

    public static DeleteRegistryRequest createDeleteEmbeddedUserGroupRequest(final RegistryTestEmbeddedUserGroupId id) {
        final DeleteRegistryRequest request = new DeleteRegistryRequest();

        final Map<String, Object> idMap = new HashMap<>();
        idMap.put("userGroupId.user", id.getUser());
        idMap.put("userGroupId.group", id.getGroup());

        request.setClassFullName(RegistryTestEmbeddedUserGroup.class.getName());
        request.setId(idMap);

        return request;
    }

    public static UpdateRegistryServiceRequest createUpdateEmbeddedUserGroupRequest(final RegistryTestEmbeddedUserGroupId id, final RegistryTestEmbeddedUserGroupId updateId, final String updateName) {
        final UpdateRegistryServiceRequest request = new UpdateRegistryServiceRequest();

        final Map<String, Object> idMap = new HashMap<>();
        idMap.put("userGroupId.user", id.getUser());
        idMap.put("userGroupId.group", id.getGroup());

        request.setClassFullName(RegistryTestEmbeddedUserGroup.class.getName());
        request.setId(idMap);
        request.setEntityData(new RegistryTestEmbeddedUserGroup(updateId, updateName));

        return request;
    }

}
