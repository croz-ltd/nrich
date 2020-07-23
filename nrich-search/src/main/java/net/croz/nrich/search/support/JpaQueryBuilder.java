package net.croz.nrich.search.support;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.search.api.model.AdditionalRestrictionResolver;
import net.croz.nrich.search.api.model.PluralAssociationRestrictionType;
import net.croz.nrich.search.api.model.SearchConfiguration;
import net.croz.nrich.search.api.model.SearchJoin;
import net.croz.nrich.search.api.model.SearchProjection;
import net.croz.nrich.search.api.model.property.SearchPropertyConfiguration;
import net.croz.nrich.search.api.model.property.SearchPropertyJoin;
import net.croz.nrich.search.api.model.subquery.SubqueryConfiguration;
import net.croz.nrich.search.model.Restriction;
import net.croz.nrich.search.model.SearchDataParserConfiguration;
import net.croz.nrich.search.parser.SearchDataParser;
import net.croz.nrich.search.util.PathResolvingUtil;
import net.croz.nrich.search.util.ProjectionListResolverUtil;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.util.DirectFieldAccessFallbackBeanWrapper;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
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

@RequiredArgsConstructor
public class JpaQueryBuilder<T> {

    private final EntityManager entityManager;

    private final Class<T> entityType;

    public <R, P> CriteriaQuery<P> buildQuery(final R request, final SearchConfiguration<T, P, R> searchConfiguration, final Sort sort) {
        Assert.notNull(request, "Search request is not defined!");
        Assert.notNull(searchConfiguration, "Search configuration is not defined!");
        Assert.notNull(sort, "Sort is not defined!");

        final Class<T> rootEntity;
        if (searchConfiguration.getRootEntityResolver() == null) {
            rootEntity = entityType;
        }
        else {
            rootEntity = searchConfiguration.getRootEntityResolver().apply(request);
        }

        Assert.notNull(rootEntity, "Root entity returned by resolver is not defined!");

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        final Class<P> resultClass = resolveResultClass(searchConfiguration, rootEntity);

        Assert.isTrue(!joinFetchExists(searchConfiguration.getJoinList()) || entityType.isAssignableFrom(resultClass), "Join Fetch is ony possible when result class is not an projection!");

        final CriteriaQuery<P> query = criteriaBuilder.createQuery(resultClass);

        final Root<T> root = query.from(rootEntity);

        applyJoinsOrFetchesToQuery(request, root, searchConfiguration.getJoinList());

        List<SearchProjection<R>> searchProjectionList = searchConfiguration.getProjectionList();
        if (!resultClass.equals(entityType) && CollectionUtils.isEmpty(searchProjectionList)) {
            searchProjectionList = ProjectionListResolverUtil.resolveSearchProjectionList(resultClass);
        }

        final List<Selection<?>> projectionList = resolveQueryProjectionList(root, searchProjectionList, request);

        if (!CollectionUtils.isEmpty(projectionList)) {
            query.multiselect(projectionList);
        }

        query.distinct(searchConfiguration.isDistinct());

        final List<Predicate> requestPredicateList = resolveQueryPredicateList(request, searchConfiguration, criteriaBuilder, root, query);
        final List<Predicate> interceptorPredicateList = resolveInterceptorPredicateList(request, searchConfiguration.getAdditionalRestrictionResolverList(), criteriaBuilder, root, query);

        applyPredicatesToQuery(criteriaBuilder, query, searchConfiguration.isAnyMatch(), requestPredicateList, interceptorPredicateList);

        if (sort.isSorted()) {
            query.orderBy(QueryUtils.toOrders(sort, root, criteriaBuilder));
        }

        return query;
    }

    public CriteriaQuery<Long> convertToCountQuery(final CriteriaQuery<?> query) {
        @SuppressWarnings("unchecked")
        final CriteriaQuery<Long> countQuery = (CriteriaQuery<Long>) query;

        query.orderBy(Collections.emptyList());

        final Root<?> root = query.getRoots().iterator().next();
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        // fetches are not allowed in count query
        root.getFetches().clear();

        if (countQuery.isDistinct()) {
            countQuery.select(builder.countDistinct(root));
        }
        else {
            countQuery.select(builder.count(root));
        }

        return countQuery;
    }

    // TODO try to use result set mapper, jpa projections require constructors with all parameters
    @SuppressWarnings("unchecked")
    private <R, P> Class<P> resolveResultClass(final SearchConfiguration<T, P, R> searchConfiguration, final Class<T> rootEntity) {
        return searchConfiguration.getResultClass() == null ? (Class<P>) rootEntity : searchConfiguration.getResultClass();
    }

    private <R> void applyJoinsOrFetchesToQuery(final R request, final Root<?> root, final List<SearchJoin<R>> joinList) {
        if (CollectionUtils.isEmpty(joinList)) {
            return;
        }

        joinList.stream()
                .filter(join -> shouldApplyJoinOrFetch(join, request))
                .forEach(searchJoin -> applyJoinOrJoinFetch(root, searchJoin));
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

    private <R> boolean shouldApplyJoinOrFetch(final SearchJoin<R> join, final R request) {
        return join.getCondition() == null || join.getCondition().test(request);
    }

    private void applyJoinOrJoinFetch(final Root<?> root, final SearchJoin<?> searchJoin) {
        final JoinType joinType = searchJoin.getJoinType() == null ? JoinType.INNER : searchJoin.getJoinType();

        final String[] pathList = PathResolvingUtil.convertToPathList(searchJoin.getPath());
        if (searchJoin.isFetch()) {
            Fetch<?, ?> fetch = null;
            for (final String path : pathList) {
                fetch = fetch == null ? root.fetch(path, joinType) : fetch.fetch(path, joinType);
            }
        }
        else {
            Join<?, ?> join = null;
            for (final String path : pathList) {
                join = join == null ? root.join(path, joinType) : join.join(path, joinType);
            }

            if (searchJoin.getAlias().indexOf('.') == -1) {
                Objects.requireNonNull(join).alias(searchJoin.getAlias());
            }
        }
    }

    private <R> boolean shouldApplyProjection(final SearchProjection<R> projection, final R request) {
        return projection.getCondition() == null || projection.getCondition().test(request);
    }

    private <P, R> List<Predicate> resolveQueryPredicateList(final R request, final SearchConfiguration<T, P, R> searchConfiguration, final CriteriaBuilder criteriaBuilder, final Root<?> root, final CriteriaQuery<?> query) {
        final Set<Restriction> restrictionList = new SearchDataParser(root.getModel(), request, SearchDataParserConfiguration.fromSearchConfiguration(searchConfiguration)).resolveRestrictionList();

        final Map<Boolean, List<Restriction>> restrictionsByType = restrictionList.stream().collect(Collectors.partitioningBy(Restriction::isPluralAttribute));

        final List<Predicate> mainQueryPredicateList = convertRestrictionListToPredicateList(restrictionsByType.get(false), root, criteriaBuilder);

        final List<Restriction> pluralRestrictionList = restrictionsByType.get(true);
        if (!CollectionUtils.isEmpty(pluralRestrictionList)) {

            if (searchConfiguration.getPluralAssociationRestrictionType() == PluralAssociationRestrictionType.JOIN) {
                mainQueryPredicateList.addAll(convertRestrictionListToPredicateList(pluralRestrictionList, root, criteriaBuilder));
            }
            else {
                final Subquery<?> subquery = createSubqueryRestriction(root.getJavaType(), root, query, criteriaBuilder, pluralRestrictionList, SearchPropertyJoin.defaultJoinById());

                mainQueryPredicateList.add(criteriaBuilder.exists(subquery));
            }
        }

        final List<Subquery<?>> subqueryList = resolveSubqueryList(request, searchConfiguration.getSearchPropertyConfiguration(), searchConfiguration.getSubqueryConfigurationList(), root, query, criteriaBuilder);

        subqueryList.forEach(subquery -> mainQueryPredicateList.add(criteriaBuilder.exists(subquery)));

        return mainQueryPredicateList;
    }

    private Subquery<?> createSubqueryRestriction(final Class<?> resultType, final Root<?> parent, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder, final Collection<Restriction> restrictionList, final SearchPropertyJoin searchPropertyJoin) {
        final Subquery<?> subquery = query.subquery(resultType);

        final Root<?> subqueryRoot = subquery.from(resultType);

        // TODO fix me, ideally select 1
        subquery.select(subqueryRoot.get("id"));

        final List<Predicate> subQueryPredicateList = convertRestrictionListToPredicateList(restrictionList, subqueryRoot, criteriaBuilder);

        final Path<?> parentPath = PathResolvingUtil.calculateFullPath(parent, PathResolvingUtil.convertToPathList(searchPropertyJoin.getParentProperty()));
        final Path<?> subqueryPath = PathResolvingUtil.calculateFullPath(subqueryRoot, PathResolvingUtil.convertToPathList(searchPropertyJoin.getChildProperty()));

        subQueryPredicateList.add(criteriaBuilder.equal(parentPath, subqueryPath));

        return subquery.where(subQueryPredicateList.toArray(new Predicate[0]));
    }

    private List<Predicate> convertRestrictionListToPredicateList(final Collection<Restriction> restrictionList, final Root<?> rootPath, final CriteriaBuilder criteriaBuilder) {
        final List<Predicate> predicateList = new ArrayList<>();

        restrictionList.forEach(restriction -> {
            if (restriction.getValue() != null) {
                final String[] pathList = PathResolvingUtil.convertToPathList(restriction.getPath());

                if (restriction.isPluralAttribute()) {
                    final String[] pluralAttributePathList = Arrays.copyOfRange(pathList, 1, pathList.length);

                    predicateList.add(restriction.getSearchOperator().asPredicate(criteriaBuilder, PathResolvingUtil.calculateFullPath(rootPath.join(pathList[0]), pluralAttributePathList), restriction.getValue()));
                }
                else {
                    predicateList.add(restriction.getSearchOperator().asPredicate(criteriaBuilder, PathResolvingUtil.calculateFullPath(rootPath, pathList), restriction.getValue()));
                }
            }
        });

        return predicateList;
    }

    // TODO enable join usage or subquery?
    private <R> List<Subquery<?>> resolveSubqueryList(final R request, final SearchPropertyConfiguration searchPropertyConfiguration, final List<SubqueryConfiguration> subqueryConfigurationList, final Root<?> root, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder) {
        if (CollectionUtils.isEmpty(subqueryConfigurationList)) {
            return Collections.emptyList();
        }

        return subqueryConfigurationList.stream()
                .map(subqueryConfiguration -> buildSubquery(request, searchPropertyConfiguration, root, query, criteriaBuilder, subqueryConfiguration))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private <R> Subquery<?> buildSubquery(final R request, final SearchPropertyConfiguration searchPropertyConfiguration, final Root<?> root, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder, final SubqueryConfiguration subqueryConfiguration) {
        final ManagedType<?> subqueryRoot = entityManager.getMetamodel().managedType(subqueryConfiguration.getRootEntity());

        final Set<Restriction> subqueryRestrictionList;
        if (subqueryConfiguration.getRestrictionPropertyHolder() == null) {
            final String propertyPrefix = subqueryConfiguration.getPropertyPrefix() == null ? StringUtils.uncapitalize(subqueryConfiguration.getRootEntity().getSimpleName()) : subqueryConfiguration.getPropertyPrefix();

            subqueryRestrictionList = new SearchDataParser(subqueryRoot, request, SearchDataParserConfiguration.builder().searchPropertyConfiguration(searchPropertyConfiguration).build()).resolveRestrictionList(propertyPrefix);
        }
        else {
            final Object subqueryRestrictionPropertyHolder = new DirectFieldAccessFallbackBeanWrapper(request).getPropertyValue(subqueryConfiguration.getRestrictionPropertyHolder());

            subqueryRestrictionList = new SearchDataParser(subqueryRoot, subqueryRestrictionPropertyHolder, SearchDataParserConfiguration.builder().searchPropertyConfiguration(searchPropertyConfiguration).resolvePropertyMappingUsingPrefix(true).build()).resolveRestrictionList();
        }

        Subquery<?> subquery = null;
        if (!CollectionUtils.isEmpty(subqueryRestrictionList)) {
            subquery = createSubqueryRestriction(subqueryConfiguration.getRootEntity(), root, query, criteriaBuilder, subqueryRestrictionList, subqueryConfiguration.getJoinBy());
        }

        return subquery;
    }

    private <R> Selection<?> convertToSelectionExpression(final Path<?> root, final SearchProjection<R> projection) {
        final String[] pathList = PathResolvingUtil.convertToPathList(projection.getPath());

        final Path<?> path = PathResolvingUtil.calculateFullPath(root, pathList);

        final String alias = projection.getAlias() == null ? pathList[pathList.length - 1] : projection.getAlias();

        return path.alias(alias);
    }

    private <R, P> List<Predicate> resolveInterceptorPredicateList(final R request, final List<AdditionalRestrictionResolver<T, P, R>> additionalRestrictionResolverList, final CriteriaBuilder criteriaBuilder, final Root<T> root, final CriteriaQuery<P> query) {
        return Optional.ofNullable(additionalRestrictionResolverList).orElse(Collections.emptyList()).stream()
                .map(interceptor -> interceptor.resolvePredicateList(criteriaBuilder, query, root, request))
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private void applyPredicatesToQuery(final CriteriaBuilder criteriaBuilder, final CriteriaQuery<?> query, final boolean anyMatch, final List<Predicate> requestPredicateList, final List<Predicate> interceptorPredicateList) {
        final List<Predicate> fullPredicateList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(requestPredicateList)) {
            final Predicate requestPredicate = anyMatch ? criteriaBuilder.or(requestPredicateList.toArray(new Predicate[0])) : criteriaBuilder.and(requestPredicateList.toArray(new Predicate[0]));

            fullPredicateList.add(requestPredicate);
        }

        if (!CollectionUtils.isEmpty(interceptorPredicateList)) {
            final Predicate interceptorPredicate = criteriaBuilder.and(interceptorPredicateList.toArray(new Predicate[0]));

            fullPredicateList.add(interceptorPredicate);
        }

        query.where(fullPredicateList.toArray(new Predicate[0]));
    }

    private <R> boolean joinFetchExists(final List<SearchJoin<R>> joinList) {
        return Optional.ofNullable(joinList).orElse(Collections.emptyList()).stream()
                .anyMatch(SearchJoin::isFetch);
    }
}
