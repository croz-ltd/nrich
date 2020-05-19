package net.croz.nrich.search.model;

import lombok.Data;
import net.croz.nrich.search.request.SearchRequest;

import java.util.function.Function;

@Data
public final class DefaultRootEntityResolver<T, S extends SearchRequest<T, S>> implements Function<S, Class<T>> {

    private final Class<T> rootEntity;

    @Override
    public Class<T> apply(S data) {
        return rootEntity;
    }
}
