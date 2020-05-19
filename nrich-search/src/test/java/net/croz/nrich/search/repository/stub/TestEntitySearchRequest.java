package net.croz.nrich.search.repository.stub;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.croz.nrich.search.model.SearchConfiguration;
import net.croz.nrich.search.request.SearchRequest;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class TestEntitySearchRequest implements SearchRequest<TestEntity, TestEntitySearchRequest> {

    private SearchConfiguration<TestEntity, TestEntitySearchRequest> searchConfiguration;

    private String name;

    private Integer ageFrom;

    private Integer ageFromIncluding;

    private Integer ageTo;

    private Integer ageToIncluding;

    private TestCollectionEntitySearchRequest collectionEntityList;

    private TestNestedEntitySearchRequest nestedEntity;

    private String collectionName;

    private String collectionEntityListName;

    private TestEntityEnum testEntityEnum;

    private TestEntityEmbeddedSearchRequest testEntityEmbedded;

    private String subqueryRestrictionName;

    private TestCollectionEntitySearchRequest subqueryRestrictionHolder;

    public TestEntitySearchRequest(final SearchConfiguration<TestEntity, TestEntitySearchRequest> searchConfiguration, final String name) {
        this.searchConfiguration = searchConfiguration;
        this.name = name;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class TestNestedEntitySearchRequest {

        private String nestedEntityName;

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class TestCollectionEntitySearchRequest {

        private String name;

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class TestEntityEmbeddedSearchRequest {

        private String embeddedName;

    }

}
