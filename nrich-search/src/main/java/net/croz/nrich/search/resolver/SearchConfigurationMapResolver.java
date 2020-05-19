package net.croz.nrich.search.resolver;

import lombok.extern.slf4j.Slf4j;
import net.croz.nrich.search.model.DefaultRootEntityResolver;
import net.croz.nrich.search.model.SearchConfiguration;
import net.croz.nrich.search.model.SearchJoin;
import net.croz.nrich.search.model.SearchProjection;
import net.croz.nrich.search.model.SearchPropertyMapping;

import javax.persistence.criteria.JoinType;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes", "unchecked"})
@Slf4j
public class SearchConfigurationMapResolver<T, P, R> {

    public SearchConfiguration<T, P, R> resolveFromMap(final Map<String, ?> mapToResolveFrom) {

        final Function<R, Class<T>> rootEntityResolver = resolveRootEntityResolverFunction(mapToResolveFrom);

        final List<SearchJoin<R>> searchJoinList = resolveJoinList(mapToResolveFrom);

        final List<SearchProjection<R>> searchProjectionList = resolveProjectionList(mapToResolveFrom);

        final List<SearchPropertyMapping> searchPropertyMappingList = resolvePropertyMappingList(mapToResolveFrom);

        return SearchConfiguration.<T, P, R>builder().rootEntityResolver(rootEntityResolver).
                joinList(searchJoinList).projectionList(searchProjectionList).propertyMappingList(searchPropertyMappingList).build();
    }

    private Function<R, Class<T>> resolveRootEntityResolverFunction(final Map<String, ?> mapToResolveFrom) {
        Function<R, Class<T>> rootEntityResolver = null;

        if (mapToResolveFrom.get("rootEntityResolver") instanceof Function) {
            rootEntityResolver = (Function<R, Class<T>>) mapToResolveFrom.get("rootEntityResolver");
        }
        else if (mapToResolveFrom.get("rootEntity") instanceof Class<?>) {
            Class<T> rootEntity = (Class<T>) mapToResolveFrom.get("rootEntity");
            rootEntityResolver = new DefaultRootEntityResolver<>(rootEntity);
        }

        return rootEntityResolver;
    }

    private List<SearchJoin<R>> resolveJoinList(final Map<String, ?> mapToResolveFrom) {
        List<SearchJoin<R>> searchJoinList = Collections.emptyList();

        if (mapToResolveFrom.get("joinList") instanceof List) {
            final List<?> rawSearchJoinList = (List) mapToResolveFrom.get("joinList");

            searchJoinList = rawSearchJoinList.stream().map(rawJoin -> {
                SearchJoin<R> searchJoin = null;

                if (rawJoin instanceof String) {
                    searchJoin = SearchJoin.innerJoin((String) rawJoin);
                }
                else if (rawJoin instanceof Map) {
                    searchJoin = parseMapToSearchJoin((Map<String, ?>) rawJoin);
                }
                return searchJoin;

            }).filter(Objects::nonNull).collect(Collectors.toList());
        }

        return searchJoinList;
    }

    private List<SearchProjection<R>> resolveProjectionList(final Map<String, ?> mapToResolveFrom) {
        List<SearchProjection<R>> searchProjectionList = Collections.emptyList();

        if (mapToResolveFrom.get("projectionList") instanceof List) {
            final List<?> rawSearchProjectionList = (List) mapToResolveFrom.get("projectionList");

            searchProjectionList = rawSearchProjectionList.stream().map(rawProjection -> {
                SearchProjection<R> searchProjection = null;

                if (rawProjection instanceof Map) {
                    final Map<String, ?> rawProjectionMap = ((Map) rawProjection);
                    if (rawProjectionMap.size() == 1) {
                        final Map.Entry<String, String> rawProjectionEntry = (Map.Entry<String, String>) (rawProjectionMap).entrySet().iterator().next();
                        searchProjection = new SearchProjection<>(rawProjectionEntry.getKey(), rawProjectionEntry.getValue(), null);
                    }
                    else {
                        searchProjection = parseMapToSearchProjection(rawProjectionMap);
                    }

                }

                return searchProjection;
            }).filter(Objects::nonNull).collect(Collectors.toList());
        }

        return searchProjectionList;
    }

    private List<SearchPropertyMapping> resolvePropertyMappingList(final Map<String, ?> mapToResolveFrom) {
        List<SearchPropertyMapping> searchPropertyMappingList = Collections.emptyList();

        if (mapToResolveFrom.get("propertyMappingList") instanceof List) {
            final List<?> rawSearchPropertyMappingList = (List) mapToResolveFrom.get("propertyMappingList");
            searchPropertyMappingList = rawSearchPropertyMappingList.stream().map(rawProjection -> {
                SearchPropertyMapping propertyMapping = null;

                if (rawProjection instanceof Map && ((Map) rawProjection).size() == 1) {
                    final Map.Entry<String, String> rawProjectionEntry = (Map.Entry<String, String>) ((Map) rawProjection).entrySet().iterator().next();

                    propertyMapping = new SearchPropertyMapping(rawProjectionEntry.getKey(), rawProjectionEntry.getValue());
                }

                return propertyMapping;
            }).filter(Objects::nonNull).collect(Collectors.toList());
        }

        return searchPropertyMappingList;
    }

    private SearchProjection<R> parseMapToSearchProjection(final Map<String, ?> rawProjectionMap) {
        final String path = resolveIfAssignableFrom(rawProjectionMap, "path", String.class);
        final String alias = resolveIfAssignableFrom(rawProjectionMap, "alias", String.class);
        final Function<R, Boolean> joinCondition = resolveIfAssignableFrom(rawProjectionMap, "condition", Function.class);

        return new SearchProjection<>(path, alias, joinCondition);
    }

    private SearchJoin<R> parseMapToSearchJoin(final Map<String, ?> rawJoinMap) {
        final String path = resolveIfAssignableFrom(rawJoinMap, "path", String.class);
        final String alias = resolveIfAssignableFrom(rawJoinMap, "alias", String.class);
        final Function<R, Boolean> joinCondition = resolveIfAssignableFrom(rawJoinMap, "condition", Function.class);
        final JoinType joinType = resolveIfAssignableFrom(rawJoinMap, "joinType", JoinType.class);

        return new SearchJoin<>(path, alias, joinType, joinCondition);
    }

    private <E> E resolveIfAssignableFrom(final Map<String, ?> mapToResolveFrom, final String key, final Class<E> type) {
        E value = null;

        if (mapToResolveFrom.get(key) != null && mapToResolveFrom.get(key).getClass().isAssignableFrom(type)) {
            value = (E) mapToResolveFrom.get(key);
        }

        return value;
    }
}
