package net.croz.nrich.registry.data.model;

import lombok.Data;
import net.croz.nrich.search.model.SearchConfiguration;

import java.util.Map;

@Data
public class RegistrySearchConfiguration<T, P> {

    private final Class<T> registryType;

    private final SearchConfiguration<T, P, Map<String, Object>> searchConfiguration;

}
