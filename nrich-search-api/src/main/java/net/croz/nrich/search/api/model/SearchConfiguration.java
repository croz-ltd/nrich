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

package net.croz.nrich.search.api.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.croz.nrich.search.api.model.operator.SearchOperatorOverride;
import net.croz.nrich.search.api.model.property.SearchPropertyConfiguration;
import net.croz.nrich.search.api.model.property.SearchPropertyMapping;
import net.croz.nrich.search.api.model.subquery.SubqueryConfiguration;

import java.util.List;
import java.util.function.Function;

// TODO allow for easier initialization of search configuration and sync all classes (static vs builder etc)

/**
 * Holds search configuration that decides how query should be build from conditions defined in search request.
 *
 * @param <T> root persistent entity
 * @param <P> projection class (can be same as root)
 * @param <R> holder for conditions
 */
@Setter
@Getter
@Builder
public class SearchConfiguration<T, P, R> {

    /**
     * Function that resolves root entity. Can be null if root entity is same as repository entity. Useful when we want to query only subclass.
     */
    private Function<R, Class<T>> rootEntityResolver;

    /**
     * List of joins and/or fetches to be applied to query.
     */
    private List<SearchJoin<R>> joinList;

    /**
     * List of projections. Can also be resolved from result class. Useful when no result class exists, in that case Tuples are returned.
     */
    private List<SearchProjection<R>> projectionList;

    /**
     * Result class (optional). If defined only properties defined in result class are fetched.
     */
    private Class<P> resultClass;

    /**
     * Whether property prefix from request will be used to match properties defined on root entity (i.e. if entity has association to user request userName property will search for user.name).
     */
    private boolean resolvePropertyMappingUsingPrefix;

    /**
     * Explicit property mapping. Allows for defining for custom property mapping (i.e. if request userName should match userList.name).
     */
    private List<SearchPropertyMapping> propertyMappingList;

    /**
     * List of search operators override (i.e. if strings are to be searched by equality instead of like).
     */
    private List<SearchOperatorOverride> searchOperatorOverrideList;

    // TODO enable restriction type by association name
    /**
     * Decides if join or exist subquery should be used for plural associations (default is subquery).
     */
    private PluralAssociationRestrictionType pluralAssociationRestrictionType;

    /**
     * Configuration for subquery. Allows for specifying conditions for entity that don't have a direct association from root entity.
     */
    private List<SubqueryConfiguration> subqueryConfigurationList;

    /**
     * Additional restrictions to be applied to query (i.e. security restrictions).
     */
    private List<AdditionalRestrictionResolver<T, P, R>> additionalRestrictionResolverList;

    /**
     * Whether distinct operator should be applied.
     */
    private boolean distinct;

    /**
     * Whether OR operator should be used when building query (default is AND).
     */
    private boolean anyMatch;

    @Builder.Default
    private SearchPropertyConfiguration searchPropertyConfiguration = SearchPropertyConfiguration.defaultSearchPropertyConfiguration();

    public static <T, P, R> SearchConfiguration<T, P, R> emptyConfiguration() {
        return SearchConfiguration.<T, P, R>builder().searchPropertyConfiguration(SearchPropertyConfiguration.defaultSearchPropertyConfiguration()).build();
    }

    public static <T, P, R> SearchConfiguration<T, P, R> emptyConfigurationMatchingAny() {
        return SearchConfiguration.<T, P, R>builder().searchPropertyConfiguration(SearchPropertyConfiguration.defaultSearchPropertyConfiguration()).anyMatch(true).build();
    }

    public static <T, P, R> SearchConfiguration<T, P, R> emptyConfigurationWithDefaultMappingResolve() {
        return SearchConfiguration.<T, P, R>builder().searchPropertyConfiguration(SearchPropertyConfiguration.defaultSearchPropertyConfiguration()).resolvePropertyMappingUsingPrefix(true).build();
    }
}
