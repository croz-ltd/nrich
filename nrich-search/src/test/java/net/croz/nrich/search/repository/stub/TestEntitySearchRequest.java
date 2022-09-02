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

package net.croz.nrich.search.repository.stub;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
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

    private String nestedEntityDoubleNestedEntityName;

    private String nestedEntityDoubleNestedEntityNonExisting;

    private String nestedEntityNonExisting;

    private String elementCollection;

    public TestEntitySearchRequest(String name) {
        this.name = name;
    }

    @RequiredArgsConstructor
    @Getter
    public static class TestNestedEntitySearchRequest {

        private final String nestedEntityName;

    }

    @RequiredArgsConstructor
    @Getter
    public static class TestCollectionEntitySearchRequest {

        private final String name;

    }

    @RequiredArgsConstructor
    @Getter
    public static class TestEntityEmbeddedSearchRequest {

        private final String embeddedName;

    }
}
