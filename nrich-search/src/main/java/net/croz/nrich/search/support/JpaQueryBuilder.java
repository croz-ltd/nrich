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
import jakarta.persistence.criteria.FetchParent;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JpaQueryBuilder<T> {

    private final EntityManager entityManager;

    private final Class<T> entityType;

    public <R, P> CriteriaQuery<P> buildQuery(R request, SearchConfiguration<T, P, R> searchConfiguration, Sort sort) {
        validateArguments(request, searchConfiguration);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Class<T> rootEntity = resolveRootEntity(request, searchConfiguration);
        Class<P> resultClass = resolveResultClass(searchConfiguration, rootEntity);

        Assert.isTrue(!joinFetchExists(searchConfiguration.getJoinList()) || entityType.isAssignableFrom(resultClass), "Join Fetch is ony possible when result class is not an projection!");

        CriteriaQuery<P> query = criteriaBuilder.createQuery(resultClass);

        Root<T> root = query.from(rootEntity);

        applyJoinsOrFetchesToQuery(true, request, root, searchConfiguration.getJoinList());

        List<SearchProjection<R>> searchProjectionList = searchConfiguration.getProjectionList();
        if (!resultClass.equals(entityType) && CollectionUtils.isEmpty(searchProjectionList)) {
            searchProjectionList = ProjectionListResolverUtil.resolveSearchProjectionList(resultClass);
        }

        List<Selection<?>> projectionList = resolveQueryProjectionList(root, searchProjectionList, request);

        if (!CollectionUtils.isEmpty(projectionList)) {
            query.multiselect(projectionList);
        }

        query.distinct(searchConfiguration.isDistinct());

        resolveAndApplyPredicateList(request, searchConfiguration, criteriaBuilder, root, query);

        if (sort != null && sort.isSorted()) {
            query.orderBy(QueryUtils.toOrders(sort, root, criteriaBuilder));
        }

        return query;
    }

    public <R, P> CriteriaQuery<Long> buildCountQuery(R request, SearchConfiguration<T, P, R> searchConfiguration) {
        validateArguments(request, searchConfiguration);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Class<T> rootEntity = resolveRootEntity(request, searchConfiguration);
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);

        Root<T> root = query.from(rootEntity);

        applyJoinsOrFetchesToQuery(false, request, root, searchConfiguration.getJoinList());

        if (searchConfiguration.isDistinct()) {
            query.select(criteriaBuilder.countDistinct(root));
        }
        else {
            query.select(criteriaBuilder.count(root));
        }

        @SuppressWarnings("unchecked")
        CriteriaQuery<P> castedQuery = (CriteriaQuery<P>) query;

        resolveAndApplyPredicateList(request, searchConfiguration, criteriaBuilder, root, castedQuery);

        return query;
    }

    public <R, P> CriteriaQuery<Integer> buildExistsQuery(R request, SearchConfiguration<T, P, R> searchConfiguration) {
        validateArguments(request, searchConfiguration);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Class<T> rootEntity = resolveRootEntity(request, searchConfiguration);
        CriteriaQuery<Integer> query = criteriaBuilder.createQuery(Integer.class);

        Root<T> root = query.from(rootEntity);

        applyJoinsOrFetchesToQuery(false, request, root, searchConfiguration.getJoinList());

        query.select(entityManager.getCriteriaBuilder().literal(1));

        @SuppressWarnings("unchecked")
        CriteriaQuery<P> castedQuery = (CriteriaQuery<P>) query;

        resolveAndApplyPredicateList(request, searchConfiguration, criteriaBuilder, root, castedQuery);

        return query;
    }

    private <R, P> void validateArguments(R request, SearchConfiguration<T, P, R> searchConfiguration) {
        Assert.notNull(request, "Search request is not defined!");
        Assert.notNull(searchConfiguration, "Search configuration is not defined!");
    }

    private <R, P> Class<T> resolveRootEntity(R request, SearchConfiguration<T, P, R> searchConfiguration) {
        Class<T> rootEntity;
        if (searchConfiguration.getRootEntityResolver() == null) {
            rootEntity = entityType;
        }
        else {
            rootEntity = searchConfiguration.getRootEntityResolver().apply(request);
        }

        Assert.notNull(rootEntity, "Root entity returned by resolver is not defined!");

        return rootEntity;
    }

    // TODO try to use result set mapper, jpa projections require constructors with all parameters
    @SuppressWarnings("unchecked")
    private <R, P> Class<P> resolveResultClass(SearchConfiguration<T, P, R> searchConfiguration, Class<T> rootEntity) {
        return searchConfiguration.getResultClass() == null ? (Class<P>) rootEntity : searchConfiguration.getResultClass();
    }

    private <R> void applyJoinsOrFetchesToQuery(boolean applyFetch, R request, Root<?> root, List<SearchJoin<R>> joinList) {
        if (CollectionUtils.isEmpty(joinList)) {
            return;
        }

        Map<String, Fetch<?, ?>> existingFetches = new HashMap<>();
        Map<String, Join<?, ?>> existingJoins = new HashMap<>();

        joinList.stream()
            .filter(join -> shouldApplyJoinOrFetch(join, request))
            .forEach(searchJoin -> applyJoinOrJoinFetch(existingFetches, existingJoins, root, searchJoin, applyFetch));
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

    private void applyJoinOrJoinFetch(Map<String, Fetch<?, ?>> existingFetches, Map<String, Join<?, ?>> existingJoins, Root<?> root, SearchJoin<?> searchJoin, boolean applyFetch) {
        JoinType joinType = searchJoin.getJoinType() == null ? JoinType.INNER : searchJoin.getJoinType();

        String[] pathList = PathResolvingUtil.convertToPathList(searchJoin.getPath());
        if (applyFetch && searchJoin.isFetch()) {
            applyJoinOrFetch(existingFetches, pathList, (path, fetch) -> fetch == null ? root.fetch(path, joinType) : fetch.fetch(path, joinType));
        }
        else {
            applyJoinOrFetch(existingJoins, pathList, (path, join) -> join == null ? root.join(path, joinType) : ((Join<?, ?>) join).join(path, joinType));
        }
    }

    private <E> void applyJoinOrFetch(Map<String, E> existingJoinsOrFetches, String[] pathList, BiFunction<String, FetchParent<?, ?>, E> pathFunction) {
        E joinOrFetch = null;
        String currentPath = null;
        for (String path : pathList) {
            currentPath = currentPath == null ? path : PathResolvingUtil.joinPath(currentPath, path);
            if (existingJoinsOrFetches.containsKey(currentPath)) {
                joinOrFetch = existingJoinsOrFetches.get(currentPath);
            }
            else {
                joinOrFetch = pathFunction.apply(path, (FetchParent<?, ?>) joinOrFetch);
                existingJoinsOrFetches.put(currentPath, joinOrFetch);
            }
        }
    }

    private <R> boolean shouldApplyProjection(SearchProjection<R> projection, R request) {
        return projection.getCondition() == null || projection.getCondition().test(request);
    }

    private <P, R> void resolveAndApplyPredicateList(R request, SearchConfiguration<T, P, R> searchConfiguration, CriteriaBuilder criteriaBuilder, Root<T> root, CriteriaQuery<P> query) {
        List<Predicate> requestPredicateList = resolveQueryPredicateList(request, searchConfiguration, criteriaBuilder, root, query);
        List<Predicate> interceptorPredicateList = resolveInterceptorPredicateList(request, searchConfiguration.getAdditionalRestrictionResolverList(), criteriaBuilder, root, query);

        applyPredicatesToQuery(criteriaBuilder, query, searchConfiguration.isAnyMatch(), requestPredicateList, interceptorPredicateList);
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

        List<Subquery<?>> subqueryList = resolveSubqueryList(request, searchConfiguration.getSearchPropertyConfiguration(), searchConfiguration.getSubqueryConfigurationList(), root, query, criteriaBuilder);

        subqueryList.forEach(subquery -> mainQueryPredicateList.add(criteriaBuilder.exists(subquery)));

        return mainQueryPredicateList;
    }

    private Subquery<Integer> createSubqueryRestriction(Class<?> subqueryEntityType, Root<?> parent, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, Collection<Restriction> restrictionList, SearchPropertyJoin searchPropertyJoin) {
        Subquery<Integer> subquery = query.subquery(Integer.class);
        Root<?> subqueryRoot = subquery.from(subqueryEntityType);

        subquery.select(criteriaBuilder.literal(1));

        List<Predicate> subQueryPredicateList = convertRestrictionListToPredicateList(restrictionList, subqueryRoot, criteriaBuilder);

        Path<?> parentPath = PathResolvingUtil.calculateFullRestrictionPath(parent, PathResolvingUtil.convertToPathList(searchPropertyJoin.getParentProperty()));
        Path<?> subqueryPath = PathResolvingUtil.calculateFullRestrictionPath(subqueryRoot, PathResolvingUtil.convertToPathList(searchPropertyJoin.getChildProperty()));

        subQueryPredicateList.add(criteriaBuilder.equal(parentPath, subqueryPath));

        return subquery.where(subQueryPredicateList.toArray(new Predicate[0]));
    }

    private List<Predicate> convertRestrictionListToPredicateList(Collection<Restriction> restrictionList, Root<?> rootPath, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicateList = new ArrayList<>();

        restrictionList.stream().filter(Objects::nonNull).forEach(restriction -> {
            String[] pathList = PathResolvingUtil.convertToPathList(restriction.getPath());

            if (restriction.isPluralAttribute()) {
                String[] pluralAttributePathList = Arrays.copyOfRange(pathList, 1, pathList.length);
                Path<?> fullPath = PathResolvingUtil.calculateFullRestrictionPath(rootPath.join(pathList[0]), pluralAttributePathList);

                predicateList.add(restriction.getSearchOperator().asPredicate(criteriaBuilder, fullPath, restriction.getValue()));
            }
            else {
                Path<?> fullPath = PathResolvingUtil.calculateFullRestrictionPath(rootPath, pathList);

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
    private <R> List<Subquery<?>> resolveSubqueryList(R request, SearchPropertyConfiguration searchPropertyConfiguration, List<SubqueryConfiguration> subqueryConfigurationList, Root<?> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (CollectionUtils.isEmpty(subqueryConfigurationList)) {
            return Collections.emptyList();
        }

        return subqueryConfigurationList.stream().map(subqueryConfiguration -> buildSubquery(request, searchPropertyConfiguration, root, query, criteriaBuilder, subqueryConfiguration)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private <R> Subquery<Integer> buildSubquery(R request, SearchPropertyConfiguration searchPropertyConfiguration, Root<?> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, SubqueryConfiguration subqueryConfiguration) {
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

        Path<?> path = PathResolvingUtil.calculateFullSelectionPath(root, pathList);

        String alias = projection.getAlias() == null ? pathList[pathList.length - 1] : projection.getAlias();

        return path.alias(alias);
    }

    private <R, P> List<Predicate> resolveInterceptorPredicateList(R request, List<AdditionalRestrictionResolver<T, P, R>> additionalRestrictionResolverList, CriteriaBuilder criteriaBuilder, Root<T> root, CriteriaQuery<P> query) {
        return Optional.ofNullable(additionalRestrictionResolverList).orElse(Collections.emptyList()).stream().map(interceptor -> interceptor.resolvePredicateList(criteriaBuilder, query, root, request)).filter(Objects::nonNull).flatMap(List::stream).collect(Collectors.toList());
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
        return Optional.ofNullable(joinList).orElse(Collections.emptyList()).stream().anyMatch(SearchJoin::isFetch);
    }

    private String entityNamePrefix(SubqueryConfiguration subqueryConfiguration) {
        return StringUtils.uncapitalize(subqueryConfiguration.getRootEntity().getSimpleName());
    }

    private SearchDataParserConfiguration searchDataParserConfiguration(SearchPropertyConfiguration searchPropertyConfiguration, boolean resolvePropertyMappingUsingPrefix) {
        return SearchDataParserConfiguration.builder().searchPropertyConfiguration(searchPropertyConfiguration).resolvePropertyMappingUsingPrefix(resolvePropertyMappingUsingPrefix).build();
    }
}
