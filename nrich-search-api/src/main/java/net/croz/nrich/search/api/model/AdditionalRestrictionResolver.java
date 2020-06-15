package net.croz.nrich.search.api.model;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@FunctionalInterface
public interface AdditionalRestrictionResolver<T, P, R> {

    List<Predicate> resolvePredicateList(CriteriaBuilder criteriaBuilder, CriteriaQuery<P> query, Root<T> root, R request);

}
