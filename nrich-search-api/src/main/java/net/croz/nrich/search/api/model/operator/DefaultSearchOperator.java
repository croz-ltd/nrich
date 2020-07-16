package net.croz.nrich.search.api.model.operator;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.Collection;
import java.util.Objects;

// TODO check if other operators are required
@SuppressWarnings("unchecked")
public enum DefaultSearchOperator implements SearchOperator {

    CONTAINS {
        @Override
        public Predicate asPredicate(final CriteriaBuilder criteriaBuilder, final Path<?> path, final Object value) {
            return criteriaBuilder.like(criteriaBuilder.lower((Expression<String>) path), "%" + Objects.requireNonNull(value).toString().toLowerCase() + "%");
        }
    },

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
    },

    IN {
        @Override
        public Predicate asPredicate(final CriteriaBuilder criteriaBuilder, final Path<?> path, final Object value) {
            final CriteriaBuilder.In<Object> inClause = criteriaBuilder.in(path);

            ((Collection<?>) value).forEach(inClause::value);

            return inClause;
        }
    }

}
