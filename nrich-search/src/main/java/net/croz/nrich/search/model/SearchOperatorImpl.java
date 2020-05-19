package net.croz.nrich.search.model;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.Objects;

// TODO check if other operators are required
@SuppressWarnings("unchecked")
public enum SearchOperatorImpl implements SearchOperator {

    ILIKE {
        @Override
        public Predicate asPredicate(final CriteriaBuilder criteriaBuilder, final Path<?> path, final Object value) {
            return criteriaBuilder.like(criteriaBuilder.lower((Expression<String>) path), Objects.requireNonNull(value).toString().toLowerCase() + "%");
        }
    },

    LIKE {
        @Override
        public Predicate asPredicate(final CriteriaBuilder criteriaBuilder, final Path<?> path, final Object value) {
            return criteriaBuilder.like((Expression<String>) path, Objects.requireNonNull(value).toString() + "%");
        }
    },

    EQ {
        @Override
        public Predicate asPredicate(final CriteriaBuilder criteriaBuilder, final Path<?> path, final Object value) {
            return criteriaBuilder.equal(path, value);
        }
    },

    GE {
        @Override
        public Predicate asPredicate(final CriteriaBuilder criteriaBuilder, final Path<?> path, final Object value) {
            return criteriaBuilder.greaterThanOrEqualTo((Expression<Comparable<Object>>) path, (Comparable<Object>) value);
        }
    },

    LE {
        @Override
        public Predicate asPredicate(final CriteriaBuilder criteriaBuilder, final Path<?> path, final Object value) {
            return criteriaBuilder.lessThanOrEqualTo((Expression<Comparable<Object>>) path, (Comparable<Object>) value);
        }
    },

    GT {
        @Override
        public Predicate asPredicate(final CriteriaBuilder criteriaBuilder, final Path<?> path, final Object value) {
            return criteriaBuilder.greaterThan((Expression<Comparable<Object>>) path, (Comparable<Object>) value);
        }
    },

    LT {
        @Override
        public Predicate asPredicate(final CriteriaBuilder criteriaBuilder, final Path<?> path, final Object value) {
            return criteriaBuilder.lessThan((Expression<Comparable<Object>>) path, (Comparable<Object>) value);
        }
    }

}
