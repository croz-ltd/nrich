package net.croz.nrich.search.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.croz.nrich.search.request.SearchRequest;

import java.util.function.Function;

@Getter
@AllArgsConstructor
public final class DefaultRootEntityResolver<T, S extends SearchRequest<T, S>> implements Function<S, Class<T>> {

    private final Class<T> rootEntity;

    @Override
    public Class<T> apply(S data) {
        return rootEntity;
    }
}
