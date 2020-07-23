package net.croz.nrich.search.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;

/**
 * Projection that will be applied to query. Prefer using result class but if result class is not needed this can be used as an alternative.
 * @param <R>
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class SearchProjection<R> {

    /**
     * Path to property in relation to root entity.
     */
    private final String path;

    /**
     * Projection alias.
     */
    private String alias;

    /**
     * Condition that decides if projection should be applied.
     */
    private Predicate<R> condition;

}
