package net.croz.nrich.search.api.model;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
