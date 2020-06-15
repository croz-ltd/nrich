package net.croz.nrich.search.api.model;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

@FunctionalInterface
public interface SearchOperator {

    Predicate asPredicate(CriteriaBuilder criteriaBuilder, Path<?> path, Object value);

}
