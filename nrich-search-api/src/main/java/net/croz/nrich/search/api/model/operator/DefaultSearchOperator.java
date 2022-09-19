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
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.util.Collection;
import java.util.Objects;

// TODO check if other operators are required

/**
 * Contains default operators that will be used when building queries.
 */
@SuppressWarnings("unchecked")
public enum DefaultSearchOperator implements SearchOperator {

    CONTAINS {
        @Override
        public Predicate asPredicate(CriteriaBuilder criteriaBuilder, Path<?> path, Object value) {
            return criteriaBuilder.like(criteriaBuilder.lower((Expression<String>) path), "%" + Objects.requireNonNull(value).toString().toLowerCase() + "%");
        }
    },

    ILIKE {
        @Override
        public Predicate asPredicate(CriteriaBuilder criteriaBuilder, Path<?> path, Object value) {
            return criteriaBuilder.like(criteriaBuilder.lower((Expression<String>) path), Objects.requireNonNull(value).toString().toLowerCase() + "%");
        }
    },

    LIKE {
        @Override
        public Predicate asPredicate(CriteriaBuilder criteriaBuilder, Path<?> path, Object value) {
            return criteriaBuilder.like((Expression<String>) path, Objects.requireNonNull(value).toString() + "%");
        }
    },

    EQ {
        @Override
        public Predicate asPredicate(CriteriaBuilder criteriaBuilder, Path<?> path, Object value) {
            return criteriaBuilder.equal(path, value);
        }
    },

    GE {
        @Override
        public Predicate asPredicate(CriteriaBuilder criteriaBuilder, Path<?> path, Object value) {
            return criteriaBuilder.greaterThanOrEqualTo((Expression<Comparable<Object>>) path, (Comparable<Object>) value);
        }
    },

    LE {
        @Override
        public Predicate asPredicate(CriteriaBuilder criteriaBuilder, Path<?> path, Object value) {
            return criteriaBuilder.lessThanOrEqualTo((Expression<Comparable<Object>>) path, (Comparable<Object>) value);
        }
    },

    GT {
        @Override
        public Predicate asPredicate(CriteriaBuilder criteriaBuilder, Path<?> path, Object value) {
            return criteriaBuilder.greaterThan((Expression<Comparable<Object>>) path, (Comparable<Object>) value);
        }
    },

    LT {
        @Override
        public Predicate asPredicate(CriteriaBuilder criteriaBuilder, Path<?> path, Object value) {
            return criteriaBuilder.lessThan((Expression<Comparable<Object>>) path, (Comparable<Object>) value);
        }
    },

    IN {
        @Override
        public Predicate asPredicate(CriteriaBuilder criteriaBuilder, Path<?> path, Object value) {
            CriteriaBuilder.In<Object> inClause = criteriaBuilder.in(path);

            ((Collection<?>) value).forEach(inClause::value);

            return inClause;
        }
    }
}
