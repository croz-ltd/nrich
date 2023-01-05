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

package net.croz.nrich.search.api.model.operator;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

/**
 * Operator that will be used when adding value to the query.
 */
@FunctionalInterface
public interface SearchOperator {

    /**
     * Returns predicate for query.
     *
     * @param criteriaBuilder criteria builder
     * @param path            property path in relation to root query entity
     * @param value           property value
     * @return predicate that will be added to query
     */
    Predicate asPredicate(CriteriaBuilder criteriaBuilder, Path<?> path, Object value);

}
