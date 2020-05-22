package net.croz.nrich.search.repository.impl;

import net.croz.nrich.search.model.SearchConfiguration;
import net.croz.nrich.search.repository.SearchExecutor;
import net.croz.nrich.search.support.JpaQueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

// named like this so it is not picked up automatically by jpa auto configuration (executor suffix is from QueryDsl integration)
@Transactional(readOnly = true)
public class JpaSearchExecutor<T> implements SearchExecutor<T> {

    private final EntityManager entityManager;

    private final JpaQueryBuilder<T> queryBuilder;

    public JpaSearchExecutor(final EntityManager entityManager, final JpaEntityInformation<T, ?> entityInformation) {
        this.entityManager = entityManager;
        this.queryBuilder = new JpaQueryBuilder<>(entityManager, entityInformation.getJavaType());
    }

    @Override
    public <R, P> Optional<P> findOne(final R request, final SearchConfiguration<T, P, R> searchConfiguration) {
        final CriteriaQuery<P> query = queryBuilder.buildQuery(request, searchConfiguration, Sort.unsorted());

        try {
            return Optional.of(entityManager.createQuery(query).getSingleResult());
        }
        catch (final NoResultException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public <R, P> List<P> findAll(final R request, final SearchConfiguration<T, P, R> searchConfiguration) {
        final CriteriaQuery<P> query = queryBuilder.buildQuery(request, searchConfiguration, Sort.unsorted());

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public <R, P> List<P> findAll(final R request, final SearchConfiguration<T, P, R> searchConfiguration, final Sort sort) {
        final CriteriaQuery<P> query = queryBuilder.buildQuery(request, searchConfiguration, sort);

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public <R, P> Page<P> findAll(final R request, final SearchConfiguration<T, P, R> searchConfiguration, final Pageable pageable) {
        final CriteriaQuery<P> query = queryBuilder.buildQuery(request, searchConfiguration, pageable.getSort());

        if (pageable.isPaged()) {
            final TypedQuery<P> typedQuery = entityManager.createQuery(query).setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize());

            return PageableExecutionUtils.getPage(typedQuery.getResultList(), pageable, () -> executeCountQuery(query));
        }

        return new PageImpl<>(entityManager.createQuery(query).getResultList());
    }

    @Override
    public <R, P> long count(final R request, final SearchConfiguration<T, P, R> searchConfiguration) {
        return executeCountQuery(queryBuilder.buildQuery(request, searchConfiguration, Sort.unsorted()));
    }

    @Override
    public <R, P> boolean exists(final R request, final SearchConfiguration<T, P, R> searchConfiguration) {
        return executeCountQuery(queryBuilder.buildQuery(request, searchConfiguration, Sort.unsorted())) > 0;
    }

    private long executeCountQuery(final CriteriaQuery<?> query) {

        @SuppressWarnings("unchecked")
        final CriteriaQuery<Long> countQuery = (CriteriaQuery<Long>) query;

        query.orderBy(Collections.emptyList());

        final Root<?> root = query.getRoots().iterator().next();
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        if (countQuery.isDistinct()) {
            countQuery.select(builder.countDistinct(root));
        }
        else {
            countQuery.select(builder.count(root));
        }

        final List<Long> totals = entityManager.createQuery(countQuery).getResultList();

        return totals.stream().mapToLong(value -> value == null ? 0L : value).sum();
    }

}
