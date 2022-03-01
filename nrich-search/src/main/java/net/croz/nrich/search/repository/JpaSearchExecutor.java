package net.croz.nrich.search.repository;

import net.croz.nrich.search.api.model.SearchConfiguration;
import net.croz.nrich.search.api.repository.SearchExecutor;
import net.croz.nrich.search.support.JpaQueryBuilder;
import net.croz.nrich.search.util.QueryUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Optional;

// named like this so it is not picked up automatically by jpa auto configuration (executor suffix is from QueryDsl integration)
@Transactional(readOnly = true)
public class JpaSearchExecutor<T> implements SearchExecutor<T> {

    private final EntityManager entityManager;

    private final JpaQueryBuilder<T> queryBuilder;

    public JpaSearchExecutor(EntityManager entityManager, JpaEntityInformation<T, ?> entityInformation) {
        this.entityManager = entityManager;
        this.queryBuilder = new JpaQueryBuilder<>(entityManager, entityInformation.getJavaType());
    }

    @Override
    public <R, P> Optional<P> findOne(R request, SearchConfiguration<T, P, R> searchConfiguration) {
        CriteriaQuery<P> query = queryBuilder.buildQuery(request, searchConfiguration, Sort.unsorted());

        try {
            return Optional.of(entityManager.createQuery(query).getSingleResult());
        }
        catch (NoResultException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public <R, P> List<P> findAll(R request, SearchConfiguration<T, P, R> searchConfiguration) {
        CriteriaQuery<P> query = queryBuilder.buildQuery(request, searchConfiguration, Sort.unsorted());

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public <R, P> List<P> findAll(R request, SearchConfiguration<T, P, R> searchConfiguration, Sort sort) {
        CriteriaQuery<P> query = queryBuilder.buildQuery(request, searchConfiguration, sort);

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public <R, P> Page<P> findAll(R request, SearchConfiguration<T, P, R> searchConfiguration, Pageable pageable) {
        CriteriaQuery<P> query = queryBuilder.buildQuery(request, searchConfiguration, pageable.getSort());
        TypedQuery<P> typedQuery = entityManager.createQuery(query);

        if (pageable.isPaged()) {
            typedQuery.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize());

            return PageableExecutionUtils.getPage(typedQuery.getResultList(), pageable, () -> executeCountQuery(query));
        }

        return new PageImpl<>(typedQuery.getResultList());
    }

    @Override
    public <R, P> long count(R request, SearchConfiguration<T, P, R> searchConfiguration) {
        return executeCountQuery(queryBuilder.buildQuery(request, searchConfiguration, Sort.unsorted()));
    }

    @Override
    public <R, P> boolean exists(R request, SearchConfiguration<T, P, R> searchConfiguration) {
        return executeCountQuery(queryBuilder.buildQuery(request, searchConfiguration, Sort.unsorted())) > 0;
    }

    private long executeCountQuery(CriteriaQuery<?> query) {
        CriteriaQuery<Long> countQuery = queryBuilder.convertToCountQuery(query);

        List<Long> totals = entityManager.createQuery(countQuery).getResultList();

        return QueryUtil.toCountResult(totals);
    }
}
