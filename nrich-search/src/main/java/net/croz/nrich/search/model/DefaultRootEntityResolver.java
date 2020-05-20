package net.croz.nrich.search.model;

import lombok.Data;

import java.util.function.Function;

@Data
public final class DefaultRootEntityResolver<T, R> implements Function<R, Class<T>> {

    private final Class<T> rootEntity;

    @Override
    public Class<T> apply(R data) {
        return rootEntity;
    }
}
