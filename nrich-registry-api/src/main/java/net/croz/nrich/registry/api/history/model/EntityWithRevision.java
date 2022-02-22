package net.croz.nrich.registry.api.history.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Holds entity with its revision data.
 *
 * @param <T> entity type
 */
@RequiredArgsConstructor
@Getter
public class EntityWithRevision<T> {

    /**
     * Registry entity.
     */
    private final T entity;

    /**
     * Entity revision information.
     */
    private final RevisionInfo revisionInfo;

}
