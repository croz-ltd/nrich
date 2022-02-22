package net.croz.nrich.search.repository.stub;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class TestEntitySearchRequest {

    private String name;

    private List<String> nameSearchList;

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

    private String nestedEntityNestedEntityName;

    private String nestedEntityNestedEntityAliasName;

    private String testEntityEmbeddedEmbeddedName;

    public TestEntitySearchRequest(String name) {
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
