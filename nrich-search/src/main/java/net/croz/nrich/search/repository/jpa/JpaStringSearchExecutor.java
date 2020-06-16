package net.croz.nrich.search.repository.jpa;

import net.croz.nrich.search.api.model.SearchConfiguration;
import net.croz.nrich.search.api.repository.StringSearchExecutor;
import net.croz.nrich.search.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.support.JpaQueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.repository.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JpaStringSearchExecutor<T> implements StringSearchExecutor<T> {

    private final StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter;

    private final EntityManager entityManager;

    private final JpaEntityInformation<T, ?> jpaEntityInformation;

    private final JpaQueryBuilder<T> queryBuilder;

    public JpaStringSearchExecutor(final StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter, final EntityManager entityManager, final JpaEntityInformation<T, ?> jpaEntityInformation) {
        this.stringToEntityPropertyMapConverter = stringToEntityPropertyMapConverter;
        this.entityManager = entityManager;
        this.jpaEntityInformation = jpaEntityInformation;
        this.queryBuilder = new JpaQueryBuilder<>(entityManager, jpaEntityInformation.getJavaType());
    }

    @Override
    public <P> Optional<P> findOne(final String searchTerm, final List<String> propertyToSearchList, final SearchConfiguration<T, P, Map<String, Object>> searchConfiguration) {
        final Map<String, Object> searchMap = convertToMap(searchTerm, propertyToSearchList);

        final CriteriaQuery<P> query = queryBuilder.buildQuery(searchMap, searchConfiguration, Sort.unsorted());

        try {
            return Optional.of(entityManager.createQuery(query).getSingleResult());
        }
        catch (final NoResultException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public <P> List<P> findAll(final String searchTerm, final List<String> propertyToSearchList, final SearchConfiguration<T, P, Map<String, Object>> searchConfiguration) {
        final Map<String, Object> searchMap = convertToMap(searchTerm, propertyToSearchList);

        final CriteriaQuery<P> query = queryBuilder.buildQuery(searchMap, searchConfiguration, Sort.unsorted());

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public <P> List<P> findAll(final String searchTerm, final List<String> propertyToSearchList, final SearchConfiguration<T, P, Map<String, Object>> searchConfiguration, final Sort sort) {
        final Map<String, Object> searchMap = convertToMap(searchTerm, propertyToSearchList);

        final CriteriaQuery<P> query = queryBuilder.buildQuery(searchMap, searchConfiguration, sort);

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public <P> Page<P> findAll(final String searchTerm, final List<String> propertyToSearchList, final SearchConfiguration<T, P, Map<String, Object>> searchConfiguration, final Pageable pageable) {
        final Map<String, Object> searchMap = convertToMap(searchTerm, propertyToSearchList);

        final CriteriaQuery<P> query = queryBuilder.buildQuery(searchMap, searchConfiguration, pageable.getSort());
        final TypedQuery<P> typedQuery = entityManager.createQuery(query);

        if (pageable.isPaged()) {
            typedQuery.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize());

            return PageableExecutionUtils.getPage(typedQuery.getResultList(), pageable, () -> executeCountQuery(query));
        }

        return new PageImpl<>(typedQuery.getResultList());
    }

    @Override
    public <P> long count(final String searchTerm, final List<String> propertyToSearchList, final SearchConfiguration<T, P, Map<String, Object>> searchConfiguration) {
        final Map<String, Object> searchMap = convertToMap(searchTerm, propertyToSearchList);

        return executeCountQuery(queryBuilder.buildQuery(searchMap, searchConfiguration, Sort.unsorted()));
    }

    @Override
    public <P> boolean exists(final String searchTerm, final List<String> propertyToSearchList, final SearchConfiguration<T, P, Map<String, Object>> searchConfiguration) {
        final Map<String, Object> searchMap = convertToMap(searchTerm, propertyToSearchList);

        return executeCountQuery(queryBuilder.buildQuery(searchMap, searchConfiguration, Sort.unsorted())) > 0;
    }

    private Map<String, Object> convertToMap(final String searchTerm, final List<String> propertyToSearchList) {
        return stringToEntityPropertyMapConverter.convert(searchTerm, propertyToSearchList, jpaEntityInformation.getRequiredIdAttribute().getDeclaringType());
    }

    private long executeCountQuery(final CriteriaQuery<?> query) {
        final CriteriaQuery<Long> countQuery = queryBuilder.convertToCountQuery(query);

        final List<Long> totals = entityManager.createQuery(countQuery).getResultList();

        return totals.stream().mapToLong(value -> value == null ? 0L : value).sum();
    }
}
