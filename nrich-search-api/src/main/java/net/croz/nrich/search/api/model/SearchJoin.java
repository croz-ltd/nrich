package net.croz.nrich.search.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.criteria.JoinType;
import java.util.function.Predicate;

/**
 * Join or join fetch that will be applied to query if condition is satisfied (it condition is null join will always be applied).
 *
 * @param <R> search request
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class SearchJoin<R> {

    /**
     * Association path in relation to root entity.
     */
    private final String path;

    /**
     * Association alias, will be applied only if fetch is false.
     */
    private String alias;

    /**
     * Type of join (inner or left).
     */
    private JoinType joinType;

    /**
     * Condition that decides should join be applied.
     */
    private Predicate<R> condition;

    /**
     * Whether join or join fetch is applied.
     */
    private boolean fetch;

    public static <R> SearchJoin<R> innerJoin(String path) {
        return new SearchJoin<>(path, path, JoinType.INNER, null, false);
    }

    public static <R> SearchJoin<R> leftJoin(String path) {
        return new SearchJoin<>(path, path, JoinType.LEFT, null, false);
    }

    public static <R> SearchJoin<R> innerJoinFetch(String path) {
        return new SearchJoin<>(path, path, JoinType.INNER, null, true);
    }

    public static <R> SearchJoin<R> leftJoinFetch(String path) {
        return new SearchJoin<>(path, path, JoinType.LEFT, null, true);
    }
}
