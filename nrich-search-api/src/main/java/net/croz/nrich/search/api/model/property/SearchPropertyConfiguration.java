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

package net.croz.nrich.search.api.model.property;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.time.temporal.Temporal;
import java.util.List;

/**
 * Configuration that defines how properties from search request are processed (suffixes, ignored properties, join attributes etc).
 */
@Setter
@Getter
@Builder
public class SearchPropertyConfiguration {

    /**
     * List of properties from request that won't be included in creating query.
     */
    private List<String> searchIgnoredPropertyList;

    /**
     * List of classes that support range search.
     */
    private List<Class<?>> rangeQuerySupportedClassList;

    /**
     * Join attribute that will be used to join from root entity to association in subquery (can be overridden in {@link net.croz.nrich.search.api.model.subquery.SubqueryConfiguration}).
     */
    private String defaultSubqueryJoinAttribute;

    /**
     * Suffix for properties that will use greater than or equal to operator.
     */
    private String rangeQueryFromIncludingSuffix;

    /**
     * Suffix for properties that will use greater than operator.
     */
    private String rangeQueryFromSuffix;

    /**
     * Suffix for properties that will use less than or equal to operator.
     */
    private String rangeQueryToIncludingSuffix;

    /**
     * Suffix for properties that will use less than operator.
     */
    private String rangeQueryToSuffix;

    /**
     * Suffix for properties that will be used with IN operator.
     */
    private String collectionQuerySuffix;

    public static SearchPropertyConfiguration defaultSearchPropertyConfiguration() {
        return new SearchPropertyConfigurationBuilder()
            .rangeQuerySupportedClassList(List.of(Date.class, java.util.Date.class, Temporal.class, Number.class))
            .defaultSubqueryJoinAttribute("id")
            .rangeQueryFromIncludingSuffix("FromIncluding")
            .rangeQueryFromSuffix("From")
            .rangeQueryToIncludingSuffix("ToIncluding")
            .rangeQueryToSuffix("To")
            .collectionQuerySuffix("SearchList")
            .build();
    }
}
