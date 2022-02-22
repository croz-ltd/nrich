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
