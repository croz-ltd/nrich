package net.croz.nrich.search.support;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.search.model.AdditionalRestrictionResolver;
import net.croz.nrich.search.model.PluralAssociationRestrictionType;
import net.croz.nrich.search.model.SearchConfiguration;
import net.croz.nrich.search.model.SearchDataParserConfiguration;
import net.croz.nrich.search.model.SearchFieldConfiguration;
import net.croz.nrich.search.model.SearchJoin;
import net.croz.nrich.search.model.SearchProjection;
import net.croz.nrich.search.model.SearchPropertyJoin;
import net.croz.nrich.search.model.SubqueryConfiguration;
import net.croz.nrich.search.parser.SearchDataParser;
import net.croz.nrich.search.util.PathResolvingUtil;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.util.DirectFieldAccessFallbackBeanWrapper;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
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

@RequiredArgsConstructor
public class JpaQueryBuilder<T> {

    private final EntityManager entityManager;

    private final Class<T> entityType;

    public <R, P> CriteriaQuery<P> buildQuery(final R request, final SearchConfiguration<T, P, R> searchConfiguration, final Sort sort) {
        Assert.notNull(request, "Search request is not defined!");
        Assert.notNull(searchConfiguration, "Search configuration is not defined!");
        Assert.notNull(sort, "Sort configuration is not defined!");

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

        final CriteriaQuery<P> query = criteriaBuilder.createQuery(resultClass);

        final Root<T> root = query.from(rootEntity);

        applyJoinsToQuery(request, root, searchConfiguration.getJoinList());

        final List<Selection<?>> projectionList = resolveQueryProjectionList(root, searchConfiguration.getProjectionList(), request);

        if (!CollectionUtils.isEmpty(projectionList)) {
            query.multiselect(projectionList);
        }

        query.distinct(searchConfiguration.isDistinct());

        final List<Predicate> requestPredicateList = resolveQueryPredicateList(request, searchConfiguration, criteriaBuilder, root, query);
        final List<Predicate> interceptorPredicateList = resolveInterceptorPredicateList(request, searchConfiguration.getAdditionalRestrictionResolverList(), criteriaBuilder, root, query);

        final Predicate requestPredicate = searchConfiguration.isAnyMatch() ? criteriaBuilder.or(requestPredicateList.toArray(new Predicate[0])) : criteriaBuilder.and(requestPredicateList.toArray(new Predicate[0]));
        final Predicate interceptorPredicate = criteriaBuilder.and(interceptorPredicateList.toArray(new Predicate[0]));

        query.where(requestPredicate, interceptorPredicate);

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

    private <P, R> List<Predicate> resolveQueryPredicateList(final R request, final SearchConfiguration<T, P, R> searchConfiguration, final CriteriaBuilder criteriaBuilder, final Root<?> root, final CriteriaQuery<?> query) {
        final Set<SearchDataParser.Restriction> restrictionList = new SearchDataParser(root.getModel(), request, SearchDataParserConfiguration.fromSearchConfiguration(searchConfiguration)).resolveRestrictionList();

        final Map<Boolean, List<SearchDataParser.Restriction>> restrictionsByType = restrictionList.stream().collect(Collectors.partitioningBy(SearchDataParser.Restriction::isPluralAttribute));

        final List<Predicate> mainQueryPredicateList = convertRestrictionListToPredicateList(restrictionsByType.get(false), root, criteriaBuilder);

        final List<SearchDataParser.Restriction> pluralRestrictionList = restrictionsByType.get(true);
        if (!CollectionUtils.isEmpty(pluralRestrictionList)) {

            if (searchConfiguration.getPluralAssociationRestrictionType() == PluralAssociationRestrictionType.JOIN) {
                mainQueryPredicateList.addAll(convertRestrictionListToPredicateList(pluralRestrictionList, root, criteriaBuilder));
            }
            else {
                final Subquery<?> subquery = createSubqueryRestriction(root.getJavaType(), root, query, criteriaBuilder, pluralRestrictionList, SearchPropertyJoin.defaultJoinById());

                mainQueryPredicateList.add(criteriaBuilder.exists(subquery));
            }
        }

        final List<Subquery<?>> subqueryList = resolveSubqueryList(request, searchConfiguration.getSearchFieldConfiguration(), searchConfiguration.getSubqueryConfigurationList(), root, query, criteriaBuilder);

        subqueryList.forEach(subquery -> mainQueryPredicateList.add(criteriaBuilder.exists(subquery)));

        return mainQueryPredicateList;
    }

    private Subquery<?> createSubqueryRestriction(final Class<?> resultType, final Root<?> parent, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder, final Collection<SearchDataParser.Restriction> restrictionList, final SearchPropertyJoin searchPropertyJoin) {
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

    private List<Predicate> convertRestrictionListToPredicateList(final Collection<SearchDataParser.Restriction> restrictionList, final Root<?> rootPath, final CriteriaBuilder criteriaBuilder) {
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
    private <R> List<Subquery<?>> resolveSubqueryList(final R request, final SearchFieldConfiguration searchFieldConfiguration, final List<SubqueryConfiguration> subqueryConfigurationList, final Root<?> root, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder) {
        if (CollectionUtils.isEmpty(subqueryConfigurationList)) {
            return Collections.emptyList();
        }

        return subqueryConfigurationList.stream()
                .map(subqueryConfiguration -> buildSubquery(request, searchFieldConfiguration, root, query, criteriaBuilder, subqueryConfiguration))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private <R> Subquery<?> buildSubquery(final R request, final SearchFieldConfiguration searchFieldConfiguration, final Root<?> root, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder, final SubqueryConfiguration subqueryConfiguration) {
        final ManagedType<?> subqueryRoot = entityManager.getMetamodel().managedType(subqueryConfiguration.getRootEntity());

        final Set<SearchDataParser.Restriction> subqueryRestrictionList;
        if (subqueryConfiguration.getRestrictionPropertyHolder() == null) {
            final String propertyPrefix = subqueryConfiguration.getPropertyPrefix() == null ? StringUtils.uncapitalize(subqueryConfiguration.getRootEntity().getSimpleName()) : subqueryConfiguration.getPropertyPrefix();

            subqueryRestrictionList = new SearchDataParser(subqueryRoot, request, SearchDataParserConfiguration.builder().searchFieldConfiguration(searchFieldConfiguration).build()).resolveRestrictionList(propertyPrefix);
        }
        else {
            final Object subqueryRestrictionPropertyHolder = new DirectFieldAccessFallbackBeanWrapper(request).getPropertyValue(subqueryConfiguration.getRestrictionPropertyHolder());

            subqueryRestrictionList = new SearchDataParser(subqueryRoot, subqueryRestrictionPropertyHolder, SearchDataParserConfiguration.builder().searchFieldConfiguration(searchFieldConfiguration).resolveFieldMappingUsingPrefix(true).build()).resolveRestrictionList();
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
}
