package net.croz.nrich.search.model;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

public interface SearchOperator {

    Predicate asPredicate(CriteriaBuilder criteriaBuilder, Path<?> path, Object value);

}
