package net.croz.nrich.registry.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.search.api.model.SearchConfiguration;

import java.util.Map;

@RequiredArgsConstructor
@Getter
public class RegistryDataConfiguration<T, P> {

    private final Class<T> registryType;

    private final SearchConfiguration<T, P, Map<String, Object>> searchConfiguration;

}
