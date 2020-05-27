package net.croz.nrich.registry.data.testutil;

import net.croz.nrich.registry.data.model.SearchParameter;
import net.croz.nrich.registry.data.request.ListRegistryRequest;
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

    public static ListRegistryRequest createRegistryListRequest(final String classFullName, final String query) {
        SearchParameter searchParameter = null;
        if (query != null) {
            searchParameter = new SearchParameter();
            searchParameter.setPropertyNameList(Arrays.asList("age", "name"));
            searchParameter.setQuery(query);
        }

        final ListRegistryRequest listRegistryRequest = new ListRegistryRequest();

        listRegistryRequest.setClassFullName(classFullName);
        listRegistryRequest.setPageNumber(0);
        listRegistryRequest.setPageSize(10);
        listRegistryRequest.setSearchParameter(searchParameter);

        return listRegistryRequest;
    }
}
