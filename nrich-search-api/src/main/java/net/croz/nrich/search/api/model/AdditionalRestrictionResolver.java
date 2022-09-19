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

package net.croz.nrich.search.api.model;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;

/**
 * Resolves additional restrictions for query (i.e. security restrictions).
 *
 * @param <T> root persistent entity
 * @param <P> projection class (can be same as root)
 * @param <R> holder for conditions
 */
@FunctionalInterface
public interface AdditionalRestrictionResolver<T, P, R> {

    /**
     * Returns a list of predicates that will be applied to main query.
     *
     * @param criteriaBuilder criteria builder
     * @param query           criteria query
     * @param root            root query entity
     * @param request         search request
     * @return list of predicates
     */
    List<Predicate> resolvePredicateList(CriteriaBuilder criteriaBuilder, CriteriaQuery<P> query, Root<T> root, R request);

}
