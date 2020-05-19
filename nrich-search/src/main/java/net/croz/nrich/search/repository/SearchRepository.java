package net.croz.nrich.search.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.croz.nrich.search.model.PluralAssociationRestrictionType;
import net.croz.nrich.search.model.Restriction;
import net.croz.nrich.search.model.SearchConfiguration;
import net.croz.nrich.search.model.SearchDataParserConfiguration;
import net.croz.nrich.search.model.SearchJoin;
import net.croz.nrich.search.model.SearchProjection;
import net.croz.nrich.search.model.SearchPropertyJoin;
import net.croz.nrich.search.model.SubqueryConfiguration;
import net.croz.nrich.search.parser.SearchDataParser;
import net.croz.nrich.search.properties.SearchProperties;
import net.croz.nrich.search.request.SearchRequest;
import net.croz.nrich.search.support.PathResolvingUtil;
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
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class SearchRepository<T, S extends SearchRequest<T, S>> {

    private final EntityManager entityManager;

    private final SearchProperties searchProperties;

    public List<?> findAll(final S request) {
        final CriteriaQuery<?> query = prepareQuery(request);

        return entityManager.createQuery(query).getResultList();
    }

    private CriteriaQuery<?> prepareQuery(final S request) {
        final SearchConfiguration<T, S> searchConfiguration = request.getSearchConfiguration();

        Assert.notNull(searchConfiguration, "Search configuration is not defined for request!");

        Assert.notNull(searchConfiguration.getRootEntityResolver(), "Root entity resolver is not defined!");

        final Class<T> rootEntity = searchConfiguration.getRootEntityResolver().apply(request);

        Assert.notNull(rootEntity, "Root entity for search is not defined!");

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        final Class<?> resultClass = resolveResultClass(searchConfiguration, rootEntity);

        final CriteriaQuery<?> query = criteriaBuilder.createQuery(resultClass);

        final Root<?> root = query.from(rootEntity);

        applyJoinsToQuery(request, root, searchConfiguration.getJoinList());

        final List<Selection<?>> projectionList = resolveQueryProjectionList(root, searchConfiguration.getProjectionList(), request);

        if (!CollectionUtils.isEmpty(projectionList)) {
            query.multiselect(projectionList);
        }

        final List<Predicate> predicateList = resolveQueryPredicateList(request, searchConfiguration, root, query, criteriaBuilder);

        if (!CollectionUtils.isEmpty(predicateList)) {
            query.where(predicateList.toArray(new Predicate[0]));
        }

        return query;
    }

    // TODO try to use result set mapper, jpa projections require constructors with all parameters
    private Class<?> resolveResultClass(final SearchConfiguration<T, S> searchConfiguration, final Class<T> rootEntity) {
        return searchConfiguration.getResultClass() == null ? rootEntity : searchConfiguration.getResultClass();
    }

    private List<Selection<Object>> applyJoinsToQuery(final S request, final Root<?> root, final List<SearchJoin<S>> joinList) {
        if (CollectionUtils.isEmpty(joinList)) {
            return Collections.emptyList();
        }
        return joinList.stream().filter(join -> shouldApplyJoin(join, request)).map(searchJoin -> root.join(searchJoin.getPath(), searchJoin.getJoinType() == null ? JoinType.INNER : searchJoin.getJoinType()).alias(searchJoin.getAlias())).collect(Collectors.toList());
    }

    private List<Selection<?>> resolveQueryProjectionList(final Root<?> root, final List<SearchProjection<S>> projectionList, final S request) {
        if (CollectionUtils.isEmpty(projectionList)) {
            return Collections.emptyList();
        }

        return projectionList.stream().filter(projection -> shouldApplyProjection(projection, request)).map(projection -> {
            final String[] pathList = PathResolvingUtil.convertToPathList(projection.getPath());

            final Path<?> path = calculateFullPath(root, pathList);

            final String alias = projection.getAlias() == null ? pathList[pathList.length - 1] : projection.getAlias();

            return path.alias(alias);

        }).collect(Collectors.toList());
    }

    private boolean shouldApplyJoin(final SearchJoin<S> join, final S request) {
        return join.getCondition() == null || join.getCondition().apply(request);
    }

    private boolean shouldApplyProjection(final SearchProjection<S> projection, final S request) {
        return projection.getCondition() == null || projection.getCondition().apply(request);
    }

    private List<Predicate> resolveQueryPredicateList(final S request, final SearchConfiguration<T, S> searchConfiguration, final Root<?> root, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder) {
        final Set<Restriction> restrictionList = new SearchDataParser(searchProperties, root.getModel(), request, SearchDataParserConfiguration.fromSearchConfiguration(searchConfiguration)).resolveRestrictionList();

        final Map<Boolean, List<Restriction>> restrictionsByType = restrictionList.stream().collect(Collectors.partitioningBy(Restriction::isPluralAttribute));

        final List<Predicate> mainQueryPredicateList = convertRestrictionListToPredicateList(restrictionsByType.get(false), root, criteriaBuilder);

        if (!CollectionUtils.isEmpty(restrictionsByType.get(true))) {

            if (request.getSearchConfiguration().getPluralAssociationRestrictionType() == PluralAssociationRestrictionType.JOIN) {
                mainQueryPredicateList.addAll(convertRestrictionListToPredicateList(restrictionsByType.get(true), root, criteriaBuilder));
            }
            else {
                final Subquery<?> subquery = createSubqueryRestriction(root.getJavaType(), root, query, criteriaBuilder, restrictionsByType.get(true), SearchPropertyJoin.defaultJoinById());

                mainQueryPredicateList.add(criteriaBuilder.exists(subquery));
            }
        }

        final List<Subquery<?>> subqueryList = resolveSubqueryList(request, searchConfiguration.getSubqueryConfigurationList(), root, query, criteriaBuilder);

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
    private List<Subquery<?>> resolveSubqueryList(final S request, final List<SubqueryConfiguration> subqueryConfigurationList, final Root<?> root, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder) {
        if (CollectionUtils.isEmpty(subqueryConfigurationList)) {
            return Collections.emptyList();
        }

        return subqueryConfigurationList.stream().map(subqueryConfiguration -> {
            final ManagedType<?> subqueryType = resolveManagedTypeByClass(subqueryConfiguration.getRootEntity());

            Subquery<?> subquery = null;
            final Set<Restriction> subqueryRestrictionList;
            if (subqueryConfiguration.getRestrictionPropertyHolder() == null) {
                final String propertyPrefix = subqueryConfiguration.getPropertyPrefix() == null ? StringUtils.uncapitalize(subqueryConfiguration.getRootEntity().getSimpleName()) : subqueryConfiguration.getPropertyPrefix();

                subqueryRestrictionList = new SearchDataParser(searchProperties, subqueryType, request, new SearchDataParserConfiguration()).resolveRestrictionList(propertyPrefix);
            }
            else {
                final Object subqueryRestrictionPropertyHolder = new DirectFieldAccessFallbackBeanWrapper(request).getPropertyValue(subqueryConfiguration.getRestrictionPropertyHolder());
                subqueryRestrictionList = new SearchDataParser(searchProperties, subqueryType, subqueryRestrictionPropertyHolder, SearchDataParserConfiguration.builder().resolveFieldMappingUsingPrefix(true).build()).resolveRestrictionList();
            }

            if (!CollectionUtils.isEmpty(subqueryRestrictionList)) {
                subquery = createSubqueryRestriction(subqueryConfiguration.getRootEntity(), root, query, criteriaBuilder, subqueryRestrictionList, subqueryConfiguration.getJoinBy());
            }

            return subquery;

        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private ManagedType<?> resolveManagedTypeByClass(final Class<?> type) {
        return entityManager.getEntityManagerFactory().getMetamodel().getManagedTypes().stream().filter(managedType -> managedType.getJavaType().equals(type)).findFirst().orElse(null);
    }

}
