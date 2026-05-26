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

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

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

    /**
     * Returns predicate for query honoring the supplied {@link SearchOperatorContext}. Default implementation ignores the context and delegates to
     * {@link #asPredicate(CriteriaBuilder, Path, Object)} so existing operator implementations remain functional.
     * Custom wildcard-based operators (e.g. those wrapping the value with {@code %}) <strong>must</strong> override this method and apply
     * {@link SearchOperatorContext#escapeCharacter()} to the value, otherwise user supplied {@code %} and {@code _} characters will be treated as {@code LIKE} wildcards. Built-in
     * wildcard operators in {@link DefaultSearchOperator} already do this.
     *
     * @param criteriaBuilder criteria builder
     * @param path            property path in relation to root query entity
     * @param value           property value
     * @param context         search operator context
     * @return predicate that will be added to query
     */
    default Predicate asPredicate(CriteriaBuilder criteriaBuilder, Path<?> path, Object value, SearchOperatorContext context) {
        return asPredicate(criteriaBuilder, path, value);
    }

}
