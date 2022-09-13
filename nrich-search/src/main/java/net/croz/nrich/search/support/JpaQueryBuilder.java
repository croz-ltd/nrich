/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

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

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.metamodel.ManagedType;
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

    public <R, P> CriteriaQuery<P> buildQuery(R request, SearchConfiguration<T, P, R> searchConfiguration, Sort sort) {
        Assert.notNull(request, "Search request is not defined!");
        Assert.notNull(searchConfiguration, "Search configuration is not defined!");
        Assert.notNull(sort, "Sort is not defined!");

        Class<T> rootEntity;
        if (searchConfiguration.getRootEntityResolver() == null) {
            rootEntity = entityType;
        }
        else {
            rootEntity = searchConfiguration.getRootEntityResolver().apply(request);
        }

        Assert.notNull(rootEntity, "Root entity returned by resolver is not defined!");

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        Class<P> resultClass = resolveResultClass(searchConfiguration, rootEntity);

        Assert.isTrue(!joinFetchExists(searchConfiguration.getJoinList()) || entityType.isAssignableFrom(resultClass), "Join Fetch is ony possible when result class is not an projection!");

        CriteriaQuery<P> query = criteriaBuilder.createQuery(resultClass);

        Root<T> root = query.from(rootEntity);

        applyJoinsOrFetchesToQuery(request, root, searchConfiguration.getJoinList());

        List<SearchProjection<R>> searchProjectionList = searchConfiguration.getProjectionList();
        if (!resultClass.equals(entityType) && CollectionUtils.isEmpty(searchProjectionList)) {
            searchProjectionList = ProjectionListResolverUtil.resolveSearchProjectionList(resultClass);
        }

        List<Selection<?>> projectionList = resolveQueryProjectionList(root, searchProjectionList, request);

        if (!CollectionUtils.isEmpty(projectionList)) {
            query.multiselect(projectionList);
        }

        query.distinct(searchConfiguration.isDistinct());

        List<Predicate> requestPredicateList = resolveQueryPredicateList(request, searchConfiguration, criteriaBuilder, root, query);
        List<Predicate> interceptorPredicateList = resolveInterceptorPredicateList(request, searchConfiguration.getAdditionalRestrictionResolverList(), criteriaBuilder, root, query);

        applyPredicatesToQuery(criteriaBuilder, query, searchConfiguration.isAnyMatch(), requestPredicateList, interceptorPredicateList);

        if (sort.isSorted()) {
            query.orderBy(QueryUtils.toOrders(sort, root, criteriaBuilder));
        }

        return query;
    }

    public CriteriaQuery<Long> convertToCountQuery(CriteriaQuery<?> query) {
        @SuppressWarnings("unchecked")
        CriteriaQuery<Long> countQuery = (CriteriaQuery<Long>) query;

        clearSortAndFetchesFromQuery(countQuery);

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        Root<?> root = query.getRoots().iterator().next();

        if (countQuery.isDistinct()) {
            countQuery.select(builder.countDistinct(root));
        }
        else {
            countQuery.select(builder.count(root));
        }

        return countQuery;
    }

    public CriteriaQuery<Integer> convertToExistsQuery(CriteriaQuery<?> query) {
        @SuppressWarnings("unchecked")
        CriteriaQuery<Integer> existsQuery = (CriteriaQuery<Integer>) query;

        clearSortAndFetchesFromQuery(existsQuery);

        existsQuery.select(entityManager.getCriteriaBuilder().literal(1));

        return existsQuery;
    }

    // TODO try to use result set mapper, jpa projections require constructors with all parameters
    @SuppressWarnings("unchecked")
    private <R, P> Class<P> resolveResultClass(SearchConfiguration<T, P, R> searchConfiguration, Class<T> rootEntity) {
        return searchConfiguration.getResultClass() == null ? (Class<P>) rootEntity : searchConfiguration.getResultClass();
    }

    private <R> void applyJoinsOrFetchesToQuery(R request, Root<?> root, List<SearchJoin<R>> joinList) {
        if (CollectionUtils.isEmpty(joinList)) {
            return;
        }

        joinList.stream()
            .filter(join -> shouldApplyJoinOrFetch(join, request))
            .forEach(searchJoin -> applyJoinOrJoinFetch(root, searchJoin));
    }

    private <R> List<Selection<?>> resolveQueryProjectionList(Root<?> root, List<SearchProjection<R>> projectionList, R request) {
        if (CollectionUtils.isEmpty(projectionList)) {
            return Collections.emptyList();
        }

        return projectionList.stream()
            .filter(projection -> shouldApplyProjection(projection, request))
            .map(projection -> convertToSelectionExpression(root, projection))
            .collect(Collectors.toList());
    }

    private <R> boolean shouldApplyJoinOrFetch(SearchJoin<R> join, R request) {
        return join.getCondition() == null || join.getCondition().test(request);
    }

    private void applyJoinOrJoinFetch(Root<?> root, SearchJoin<?> searchJoin) {
        JoinType joinType = searchJoin.getJoinType() == null ? JoinType.INNER : searchJoin.getJoinType();

        String[] pathList = PathResolvingUtil.convertToPathList(searchJoin.getPath());
        if (searchJoin.isFetch()) {
            Fetch<?, ?> fetch = null;
            for (String path : pathList) {
                fetch = fetch == null ? root.fetch(path, joinType) : fetch.fetch(path, joinType);
            }
        }
        else {
            Join<?, ?> join = null;
            for (String path : pathList) {
                join = join == null ? root.join(path, joinType) : join.join(path, joinType);
            }

            if (searchJoin.getAlias().indexOf('.') == -1) {
                Objects.requireNonNull(join).alias(searchJoin.getAlias());
            }
        }
    }

    private <R> boolean shouldApplyProjection(SearchProjection<R> projection, R request) {
        return projection.getCondition() == null || projection.getCondition().test(request);
    }

    private <P, R> List<Predicate> resolveQueryPredicateList(R request, SearchConfiguration<T, P, R> searchConfiguration, CriteriaBuilder criteriaBuilder, Root<?> root, CriteriaQuery<?> query) {
        Set<Restriction> restrictionList = new SearchDataParser(root.getModel(), request, SearchDataParserConfiguration.fromSearchConfiguration(searchConfiguration)).resolveRestrictionList();

        Map<Boolean, List<Restriction>> restrictionsByType = restrictionList.stream().collect(Collectors.partitioningBy(Restriction::isPluralAttribute));

        List<Predicate> mainQueryPredicateList = convertRestrictionListToPredicateList(restrictionsByType.get(false), root, criteriaBuilder);

        List<Restriction> pluralRestrictionList = restrictionsByType.get(true);
        if (!CollectionUtils.isEmpty(pluralRestrictionList)) {

            if (searchConfiguration.getPluralAssociationRestrictionType() == PluralAssociationRestrictionType.JOIN) {
                mainQueryPredicateList.addAll(convertRestrictionListToPredicateList(pluralRestrictionList, root, criteriaBuilder));
            }
            else {
                SearchPropertyJoin searchPropertyJoin = resolveSearchPropertyJoin(root);
                Subquery<Integer> subquery = createSubqueryRestriction(root.getJavaType(), root, query, criteriaBuilder, pluralRestrictionList, searchPropertyJoin);

                mainQueryPredicateList.add(criteriaBuilder.exists(subquery));
            }
        }

        List<Subquery<?>> subqueryList = resolveSubqueryList(
            request, searchConfiguration.getSearchPropertyConfiguration(), searchConfiguration.getSubqueryConfigurationList(), root, query, criteriaBuilder
        );

        subqueryList.forEach(subquery -> mainQueryPredicateList.add(criteriaBuilder.exists(subquery)));

        return mainQueryPredicateList;
    }

    private Subquery<Integer> createSubqueryRestriction(Class<?> subqueryEntityType, Root<?> parent, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder,
                                                        Collection<Restriction> restrictionList, SearchPropertyJoin searchPropertyJoin) {
        Subquery<Integer> subquery = query.subquery(Integer.class);
        Root<?> subqueryRoot = subquery.from(subqueryEntityType);

        subquery.select(criteriaBuilder.literal(1));

        List<Predicate> subQueryPredicateList = convertRestrictionListToPredicateList(restrictionList, subqueryRoot, criteriaBuilder);

        Path<?> parentPath = PathResolvingUtil.calculateFullPath(parent, PathResolvingUtil.convertToPathList(searchPropertyJoin.getParentProperty()));
        Path<?> subqueryPath = PathResolvingUtil.calculateFullPath(subqueryRoot, PathResolvingUtil.convertToPathList(searchPropertyJoin.getChildProperty()));

        subQueryPredicateList.add(criteriaBuilder.equal(parentPath, subqueryPath));

        return subquery.where(subQueryPredicateList.toArray(new Predicate[0]));
    }

    private List<Predicate> convertRestrictionListToPredicateList(Collection<Restriction> restrictionList, Root<?> rootPath, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicateList = new ArrayList<>();

        restrictionList.stream().filter(Objects::nonNull).forEach(restriction -> {
            String[] pathList = PathResolvingUtil.convertToPathList(restriction.getPath());

            if (restriction.isPluralAttribute()) {
                String[] pluralAttributePathList = Arrays.copyOfRange(pathList, 1, pathList.length);
                Path<?> fullPath = PathResolvingUtil.calculateFullPath(rootPath.join(pathList[0]), pluralAttributePathList);

                predicateList.add(restriction.getSearchOperator().asPredicate(criteriaBuilder, fullPath, restriction.getValue()));
            }
            else {
                Path<?> fullPath = PathResolvingUtil.calculateFullPath(rootPath, pathList);

                predicateList.add(restriction.getSearchOperator().asPredicate(criteriaBuilder, fullPath, restriction.getValue()));
            }
        });

        return predicateList;
    }

    private SearchPropertyJoin resolveSearchPropertyJoin(Root<?> root) {
      String idName = root.getModel().getId(root.getModel().getIdType().getJavaType()).getName();

      return new SearchPropertyJoin(idName, idName);
    }

    // TODO enable join usage or subquery?
    private <R> List<Subquery<?>> resolveSubqueryList(R request, SearchPropertyConfiguration searchPropertyConfiguration, List<SubqueryConfiguration> subqueryConfigurationList, Root<?> root,
                                                      CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (CollectionUtils.isEmpty(subqueryConfigurationList)) {
            return Collections.emptyList();
        }

        return subqueryConfigurationList.stream()
            .map(subqueryConfiguration -> buildSubquery(request, searchPropertyConfiguration, root, query, criteriaBuilder, subqueryConfiguration))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private <R> Subquery<Integer> buildSubquery(R request, SearchPropertyConfiguration searchPropertyConfiguration, Root<?> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder,
                                                SubqueryConfiguration subqueryConfiguration) {
        ManagedType<?> subqueryRoot = entityManager.getMetamodel().managedType(subqueryConfiguration.getRootEntity());

        Set<Restriction> subqueryRestrictionList;
        if (subqueryConfiguration.getRestrictionPropertyHolder() == null) {
            String propertyPrefix = subqueryConfiguration.getPropertyPrefix() == null ? entityNamePrefix(subqueryConfiguration) : subqueryConfiguration.getPropertyPrefix();
            SearchDataParser searchDataParser = new SearchDataParser(subqueryRoot, request, searchDataParserConfiguration(searchPropertyConfiguration, false));

            subqueryRestrictionList = searchDataParser.resolveRestrictionList(propertyPrefix);
        }
        else {
            Object subqueryRestrictionPropertyHolder = new DirectFieldAccessFallbackBeanWrapper(request).getPropertyValue(subqueryConfiguration.getRestrictionPropertyHolder());
            SearchDataParser searchDataParser = new SearchDataParser(subqueryRoot, subqueryRestrictionPropertyHolder, searchDataParserConfiguration(searchPropertyConfiguration, true));

            subqueryRestrictionList = searchDataParser.resolveRestrictionList();
        }

        Subquery<Integer> subquery = null;
        if (!CollectionUtils.isEmpty(subqueryRestrictionList)) {
            subquery = createSubqueryRestriction(subqueryConfiguration.getRootEntity(), root, query, criteriaBuilder, subqueryRestrictionList, subqueryConfiguration.getJoinBy());
        }

        return subquery;
    }

    private <R> Selection<?> convertToSelectionExpression(Path<?> root, SearchProjection<R> projection) {
        String[] pathList = PathResolvingUtil.convertToPathList(projection.getPath());

        Path<?> path = PathResolvingUtil.calculateFullPath(root, pathList);

        String alias = projection.getAlias() == null ? pathList[pathList.length - 1] : projection.getAlias();

        return path.alias(alias);
    }

    private <R, P> List<Predicate> resolveInterceptorPredicateList(R request, List<AdditionalRestrictionResolver<T, P, R>> additionalRestrictionResolverList, CriteriaBuilder criteriaBuilder,
                                                                   Root<T> root, CriteriaQuery<P> query) {
        return Optional.ofNullable(additionalRestrictionResolverList).orElse(Collections.emptyList()).stream()
            .map(interceptor -> interceptor.resolvePredicateList(criteriaBuilder, query, root, request))
            .filter(Objects::nonNull)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    private void applyPredicatesToQuery(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query, boolean anyMatch, List<Predicate> requestPredicateList, List<Predicate> interceptorPredicateList) {
        List<Predicate> fullPredicateList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(requestPredicateList)) {
            Predicate requestPredicate = anyMatch ? criteriaBuilder.or(requestPredicateList.toArray(new Predicate[0])) : criteriaBuilder.and(requestPredicateList.toArray(new Predicate[0]));

            fullPredicateList.add(requestPredicate);
        }

        if (!CollectionUtils.isEmpty(interceptorPredicateList)) {
            Predicate interceptorPredicate = criteriaBuilder.and(interceptorPredicateList.toArray(new Predicate[0]));

            fullPredicateList.add(interceptorPredicate);
        }

        if (!fullPredicateList.isEmpty()) {
            query.where(fullPredicateList.toArray(new Predicate[0]));
        }
    }

    private <R> boolean joinFetchExists(List<SearchJoin<R>> joinList) {
        return Optional.ofNullable(joinList).orElse(Collections.emptyList()).stream()
            .anyMatch(SearchJoin::isFetch);
    }

    private String entityNamePrefix(SubqueryConfiguration subqueryConfiguration) {
        return StringUtils.uncapitalize(subqueryConfiguration.getRootEntity().getSimpleName());
    }

    private SearchDataParserConfiguration searchDataParserConfiguration(SearchPropertyConfiguration searchPropertyConfiguration, boolean resolvePropertyMappingUsingPrefix) {
        return SearchDataParserConfiguration.builder()
            .searchPropertyConfiguration(searchPropertyConfiguration)
            .resolvePropertyMappingUsingPrefix(resolvePropertyMappingUsingPrefix)
            .build();
    }

    private void clearSortAndFetchesFromQuery(CriteriaQuery<?> query) {
        query.orderBy(Collections.emptyList());

        query.getRoots().forEach(root -> root.getFetches().clear());
    }
}
