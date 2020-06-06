package net.croz.nrich.search.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
@Getter
public final class DefaultRootEntityResolver<T, R> implements Function<R, Class<T>> {

    private final Class<T> rootEntity;

    @Override
    public Class<T> apply(R data) {
        return rootEntity;
    }
}
