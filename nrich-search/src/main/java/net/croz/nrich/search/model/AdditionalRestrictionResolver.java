package net.croz.nrich.search.model;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public interface AdditionalRestrictionResolver<T, P, R> {

    List<Predicate> resolvePredicateList(CriteriaBuilder criteriaBuilder, CriteriaQuery<P> query, Root<T> root, R request);

}
