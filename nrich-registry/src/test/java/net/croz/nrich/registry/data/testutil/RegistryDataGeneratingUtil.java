package net.croz.nrich.registry.data.testutil;

import net.croz.nrich.registry.data.model.SearchParameter;
import net.croz.nrich.registry.data.request.RegistryListRequest;
import net.croz.nrich.registry.data.stub.RegistryTestEntity;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class RegistryDataGeneratingUtil {

    private RegistryDataGeneratingUtil() {
    }

    public static List<RegistryTestEntity> createRegistryTestEntityList(final EntityManager entityManager) {
        final List<RegistryTestEntity> testEntityList = IntStream.range(0, 5).mapToObj(value -> new RegistryTestEntity(null, "name " + value, 50 + value)).collect(Collectors.toList());

        testEntityList.forEach(entityManager::persist);

        return testEntityList;
    }

    public static RegistryListRequest createRegistryListRequest(final String classFullName, final String query) {
        final SearchParameter searchParameter = new SearchParameter();

        searchParameter.setPropertyNameList(Arrays.asList("age", "name"));
        searchParameter.setQuery(query);

        final RegistryListRequest registryListRequest = new RegistryListRequest();

        registryListRequest.setClassFullName(classFullName);
        registryListRequest.setPageNumber(0);
        registryListRequest.setPageSize(10);
        registryListRequest.setSearchParameter(searchParameter);

        return registryListRequest;
    }
}
