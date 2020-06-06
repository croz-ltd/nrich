package net.croz.nrich.registry.history.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EntityWithRevision<T> {

    private final T entity;

    private final RevisionInfo revisionInfo;

}
