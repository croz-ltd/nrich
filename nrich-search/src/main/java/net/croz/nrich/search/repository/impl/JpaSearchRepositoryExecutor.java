package net.croz.nrich.search.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.croz.nrich.search.model.AdditionalRestrictionResolver;
import net.croz.nrich.search.model.PluralAssociationRestrictionType;
import net.croz.nrich.search.model.Restriction;
import net.croz.nrich.search.model.SearchConfiguration;
import net.croz.nrich.search.model.SearchDataParserConfiguration;
import net.croz.nrich.search.model.SearchFieldConfiguration;
import net.croz.nrich.search.model.SearchJoin;
import net.croz.nrich.search.model.SearchProjection;
import net.croz.nrich.search.model.SearchPropertyJoin;
import net.croz.nrich.search.model.SubqueryConfiguration;
import net.croz.nrich.search.parser.SearchDataParser;
import net.croz.nrich.search.repository.SearchExecutor;
import net.croz.nrich.search.support.PathResolvingUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.data.util.DirectFieldAccessFallbackBeanWrapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.ManagedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// named like this so it is not picked up automatically by jpa auto configuration (executor suffix is from QueryDsl integration)
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class JpaSearchRepositoryExecutor<T> implements SearchExecutor<T> {

    private final EntityManager entityManager;

    private final JpaEntityInformation<T, ?> entityInformation;

    @Override
    public <R, P> Optional<P> findOne(final R request, final SearchConfiguration<T, P, R> searchConfiguration) {
        final CriteriaQuery<P> query = prepareQuery(request, searchConfiguration, Sort.unsorted());

        try {
            return Optional.of(entityManager.createQuery(query).getSingleResult());
        }
        catch (final NoResultException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public <R, P> List<P> findAll(final R request, final SearchConfiguration<T, P, R> searchConfiguration) {
        final CriteriaQuery<P> query = prepareQuery(request, searchConfiguration, Sort.unsorted());

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public <R, P> List<P> findAll(final R request, final SearchConfiguration<T, P, R> searchConfiguration, final Sort sort) {
        final CriteriaQuery<P> query = prepareQuery(request, searchConfiguration, sort);

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public <R, P> Page<P> findAll(final R request, final SearchConfiguration<T, P, R> searchConfiguration, final Pageable pageable) {
        final CriteriaQuery<P> query = prepareQuery(request, searchConfiguration, pageable.getSort());

        if (pageable.isPaged()) {
            final TypedQuery<P> typedQuery = entityManager.createQuery(query).setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize());

            return PageableExecutionUtils.getPage(typedQuery.getResultList(), pageable, () -> executeCountQuery(query));
        }

        return new PageImpl<>(entityManager.createQuery(query).getResultList());
    }

    @Override
    public <R, P> long count(final R request, final SearchConfiguration<T, P, R> searchConfiguration) {
        return executeCountQuery(prepareQuery(request, searchConfiguration, Sort.unsorted()));
    }

    @Override
    public <R, P> boolean exists(final R request, final SearchConfiguration<T, P, R> searchConfiguration) {
        return executeCountQuery(prepareQuery(request, searchConfiguration, Sort.unsorted())) > 0;
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

    private <R, P> CriteriaQuery<P> prepareQuery(final R request, final SearchConfiguration<T, P, R> searchConfiguration, final Sort sort) {
        Assert.notNull(searchConfiguration, "Search configuration is not defined!");

        final Class<T> rootEntity;
        if (searchConfiguration.getRootEntityResolver() == null) {
            rootEntity = entityInformation.getJavaType();
        }
        else {
            rootEntity = searchConfiguration.getRootEntityResolver().apply(request);
        }

        Assert.notNull(rootEntity, "Root entity returned by resolver is not defined!");

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        final Class<P> resultClass = resolveResultClass(searchConfiguration, rootEntity);

        final CriteriaQuery<P> query = criteriaBuilder.createQuery(resultClass);

        final Root<T> root = query.from(rootEntity);

        applyJoinsToQuery(request, root, searchConfiguration.getJoinList());

        final List<Selection<?>> projectionList = resolveQueryProjectionList(root, searchConfiguration.getProjectionList(), request);

        if (!CollectionUtils.isEmpty(projectionList)) {
            query.multiselect(projectionList);
        }

        if (searchConfiguration.isDistinct()) {
            query.distinct(true);
        }

        final List<Predicate> predicateList = resolveQueryPredicateList(request, searchConfiguration, root, query, criteriaBuilder);
        final List<Predicate> interceptorPredicateList = resolveInterceptorPredicateList(searchConfiguration.getAdditionalRestrictionResolverList(), criteriaBuilder, query, root, request);

        final List<Predicate> fullPredicateList = Stream.of(predicateList, interceptorPredicateList).flatMap(Collection::stream).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(fullPredicateList)) {
            query.where(fullPredicateList.toArray(new Predicate[0]));
        }

        if (sort.isSorted()) {
            query.orderBy(QueryUtils.toOrders(sort, root, criteriaBuilder));
        }

        return query;
    }

    // TODO try to use result set mapper, jpa projections require constructors with all parameters
    @SuppressWarnings("unchecked")
    private <R, P> Class<P> resolveResultClass(final SearchConfiguration<T, P, R> searchConfiguration, final Class<T> rootEntity) {
        return searchConfiguration.getResultClass() == null ? (Class<P>) rootEntity : searchConfiguration.getResultClass();
    }

    private <R> List<Selection<Object>> applyJoinsToQuery(final R request, final Root<?> root, final List<SearchJoin<R>> joinList) {
        if (CollectionUtils.isEmpty(joinList)) {
            return Collections.emptyList();
        }

        return joinList.stream()
                .filter(join -> shouldApplyJoin(join, request))
                .map(searchJoin -> root.join(searchJoin.getPath(), searchJoin.getJoinType() == null ? JoinType.INNER : searchJoin.getJoinType()).alias(searchJoin.getAlias()))
                .collect(Collectors.toList());
    }

    private <R> List<Selection<?>> resolveQueryProjectionList(final Root<?> root, final List<SearchProjection<R>> projectionList, final R request) {
        if (CollectionUtils.isEmpty(projectionList)) {
            return Collections.emptyList();
        }

        return projectionList.stream()
                .filter(projection -> shouldApplyProjection(projection, request))
                .map(projection -> convertToSelectionExpression(root, projection))
                .collect(Collectors.toList());
    }

    private <R> boolean shouldApplyJoin(final SearchJoin<R> join, final R request) {
        return join.getCondition() == null || join.getCondition().apply(request);
    }

    private <R> boolean shouldApplyProjection(final SearchProjection<R> projection, final R request) {
        return projection.getCondition() == null || projection.getCondition().apply(request);
    }

    private <P, R> List<Predicate> resolveQueryPredicateList(final R request, final SearchConfiguration<T, P, R> searchConfiguration, final Root<?> root, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder) {
        final Set<Restriction> restrictionList = new SearchDataParser(root.getModel(), request, SearchDataParserConfiguration.fromSearchConfiguration(searchConfiguration)).resolveRestrictionList();

        final Map<Boolean, List<Restriction>> restrictionsByType = restrictionList.stream().collect(Collectors.partitioningBy(Restriction::isPluralAttribute));

        final List<Predicate> mainQueryPredicateList = convertRestrictionListToPredicateList(restrictionsByType.get(false), root, criteriaBuilder);

        if (!CollectionUtils.isEmpty(restrictionsByType.get(true))) {

            if (searchConfiguration.getPluralAssociationRestrictionType() == PluralAssociationRestrictionType.JOIN) {
                mainQueryPredicateList.addAll(convertRestrictionListToPredicateList(restrictionsByType.get(true), root, criteriaBuilder));
            }
            else {
                final Subquery<?> subquery = createSubqueryRestriction(root.getJavaType(), root, query, criteriaBuilder, restrictionsByType.get(true), SearchPropertyJoin.defaultJoinById());

                mainQueryPredicateList.add(criteriaBuilder.exists(subquery));
            }
        }

        final List<Subquery<?>> subqueryList = resolveSubqueryList(request, searchConfiguration.getSearchFieldConfiguration(), searchConfiguration.getSubqueryConfigurationList(), root, query, criteriaBuilder);

        subqueryList.forEach(subquery -> mainQueryPredicateList.add(criteriaBuilder.exists(subquery)));

        return mainQueryPredicateList;
    }

    private Subquery<?> createSubqueryRestriction(final Class<?> resultType, final Root<?> parent, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder, final Collection<Restriction> restrictionList, final SearchPropertyJoin searchPropertyJoin) {
        final Subquery<?> subquery = query.subquery(resultType);

        final Root<?> subqueryRoot = subquery.from(resultType);

        // TODO fix me, ideally select 1
        subquery.select(subqueryRoot.get("id"));

        final List<Predicate> subQueryPredicateList = convertRestrictionListToPredicateList(restrictionList, subqueryRoot, criteriaBuilder);

        subQueryPredicateList.add(criteriaBuilder.equal(calculateFullPath(parent, PathResolvingUtil.convertToPathList(searchPropertyJoin.getParentProperty())), calculateFullPath(subqueryRoot, PathResolvingUtil.convertToPathList(searchPropertyJoin.getChildProperty()))));

        return subquery.where(subQueryPredicateList.toArray(new Predicate[0]));
    }

    private List<Predicate> convertRestrictionListToPredicateList(final Collection<Restriction> restrictionList, final Root<?> rootPath, final CriteriaBuilder criteriaBuilder) {
        final List<Predicate> predicateList = new ArrayList<>();

        restrictionList.forEach(restriction -> {
            if (restriction.getValue() != null) {
                final String[] pathList = PathResolvingUtil.convertToPathList(restriction.getPath());

                if (restriction.isPluralAttribute()) {
                    final String[] pluralAttributePathList = Arrays.copyOfRange(pathList, 1, pathList.length);
                    predicateList.add(restriction.getSearchOperator().asPredicate(criteriaBuilder, calculateFullPath(rootPath.join(pathList[0]), pluralAttributePathList), restriction.getValue()));
                }
                else {
                    predicateList.add(restriction.getSearchOperator().asPredicate(criteriaBuilder, calculateFullPath(rootPath, pathList), restriction.getValue()));
                }
            }
        });

        return predicateList;
    }

    private Path<?> calculateFullPath(final Path<?> rootPath, final String[] pathList) {
        Path<?> calculatedPath = rootPath;
        for (final String currentPath : pathList) {
            calculatedPath = calculatedPath.get(currentPath);
        }

        return calculatedPath;
    }

    // TODO enable join usage or subquery?
    private <R> List<Subquery<?>> resolveSubqueryList(final R request, final SearchFieldConfiguration searchFieldConfiguration, final List<SubqueryConfiguration> subqueryConfigurationList, final Root<?> root, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder) {
        if (CollectionUtils.isEmpty(subqueryConfigurationList)) {
            return Collections.emptyList();
        }

        return subqueryConfigurationList.stream()
                .map(subqueryConfiguration -> buildSubQuery(request, searchFieldConfiguration, root, query, criteriaBuilder, subqueryConfiguration))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private <R> Subquery<?> buildSubQuery(final R request, final SearchFieldConfiguration searchFieldConfiguration, final Root<?> root, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder, final SubqueryConfiguration subqueryConfiguration) {
        final ManagedType<?> subqueryRoot = resolveManagedTypeByClass(subqueryConfiguration.getRootEntity());

        Subquery<?> subquery = null;
        final Set<Restriction> subqueryRestrictionList;
        if (subqueryConfiguration.getRestrictionPropertyHolder() == null) {
            final String propertyPrefix = subqueryConfiguration.getPropertyPrefix() == null ? StringUtils.uncapitalize(subqueryConfiguration.getRootEntity().getSimpleName()) : subqueryConfiguration.getPropertyPrefix();

            subqueryRestrictionList = new SearchDataParser(subqueryRoot, request, SearchDataParserConfiguration.builder().searchFieldConfiguration(searchFieldConfiguration).build()).resolveRestrictionList(propertyPrefix);
        }
        else {
            final Object subqueryRestrictionPropertyHolder = new DirectFieldAccessFallbackBeanWrapper(request).getPropertyValue(subqueryConfiguration.getRestrictionPropertyHolder());
            subqueryRestrictionList = new SearchDataParser(subqueryRoot, subqueryRestrictionPropertyHolder, SearchDataParserConfiguration.builder().searchFieldConfiguration(searchFieldConfiguration).resolveFieldMappingUsingPrefix(true).build()).resolveRestrictionList();
        }

        if (!CollectionUtils.isEmpty(subqueryRestrictionList)) {
            subquery = createSubqueryRestriction(subqueryConfiguration.getRootEntity(), root, query, criteriaBuilder, subqueryRestrictionList, subqueryConfiguration.getJoinBy());
        }

        return subquery;
    }

    private ManagedType<?> resolveManagedTypeByClass(final Class<?> type) {
        return entityManager.getEntityManagerFactory().getMetamodel().getManagedTypes().stream()
                .filter(managedType -> managedType.getJavaType().equals(type))
                .findFirst()
                .orElse(null);
    }

    private <R> Selection<?> convertToSelectionExpression(final Path<?> root, final SearchProjection<R> projection) {
        final String[] pathList = PathResolvingUtil.convertToPathList(projection.getPath());

        final Path<?> path = calculateFullPath(root, pathList);

        final String alias = projection.getAlias() == null ? pathList[pathList.length - 1] : projection.getAlias();

        return path.alias(alias);
    }

    private <R, P> List<Predicate> resolveInterceptorPredicateList(final List<AdditionalRestrictionResolver<T, P, R>> additionalRestrictionResolverList, final CriteriaBuilder criteriaBuilder, final CriteriaQuery<P> query, final Root<T> root, final R request) {
        return Optional.ofNullable(additionalRestrictionResolverList).orElse(Collections.emptyList()).stream()
                .map(interceptor -> interceptor.resolvePredicateList(criteriaBuilder, query, root, request))
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

}
