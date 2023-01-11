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

package net.croz.nrich.search.api.model.subquery;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.croz.nrich.search.api.model.property.SearchPropertyJoin;

/**
 * Configuration for subquery. Allows specifying custom root entity, joins and resolving property values from
 * search request either by property prefix or by a separate class holding all subquery restrictions.
 */
@Setter
@Getter
@Builder
public class SubqueryConfiguration {

    /**
     * Subquery root entity.
     */
    private Class<?> rootEntity;

    /**
     * Properties by which join between entities should be performed.
     */
    private SearchPropertyJoin joinBy;

    /**
     * Prefix that will be used to resolve subquery restrictions from search request.
     */
    private String propertyPrefix;

    /**
     * Name of property that holds all subquery restrictions.
     */
    private String restrictionPropertyHolder;

}
