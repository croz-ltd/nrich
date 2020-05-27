package net.croz.nrich.registry.data.testutil;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import net.croz.nrich.registry.data.model.SearchParameter;
import net.croz.nrich.registry.data.request.CreateRegistryRequest;
import net.croz.nrich.registry.data.request.CreateRegistryServiceRequest;
import net.croz.nrich.registry.data.request.DeleteRegistryRequest;
import net.croz.nrich.registry.data.request.ListRegistryRequest;
import net.croz.nrich.registry.data.request.UpdateRegistryServiceRequest;
import net.croz.nrich.registry.data.stub.CreateRegistryTestEntityRequest;
import net.croz.nrich.registry.data.stub.RegistryTestEntity;
import net.croz.nrich.registry.data.stub.UpdateRegistryTestEntityRequest;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
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

    public static ListRegistryRequest createListRegistryRequest(final String classFullName, final String query) {
        SearchParameter searchParameter = null;
        if (query != null) {
            searchParameter = new SearchParameter();
            searchParameter.setPropertyNameList(Arrays.asList("age", "name"));
            searchParameter.setQuery(query);
        }

        final ListRegistryRequest request = new ListRegistryRequest();

        request.setClassFullName(classFullName);
        request.setPageNumber(0);
        request.setPageSize(10);
        request.setSearchParameter(searchParameter);

        return request;
    }

    public static DeleteRegistryRequest createDeleteRegistryRequest(final String classFullName, final Long id) {
        final DeleteRegistryRequest request = new DeleteRegistryRequest();

        request.setClassFullName(classFullName);
        request.setId(id);

        return request;
    }

    @SneakyThrows
    public static CreateRegistryRequest createRegistryRequest(final ObjectMapper objectMapper, final String classFullName) {
        final CreateRegistryRequest request = new CreateRegistryRequest();

        request.setClassFullName(classFullName);
        request.setEntityData(objectMapper.writeValueAsString(new CreateRegistryTestEntityRequest("name 1", 50)));

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
}
