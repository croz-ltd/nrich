package net.croz.nrich.registry.api.core.model;

import lombok.Builder;
import lombok.Getter;
import net.croz.nrich.search.api.model.SearchConfiguration;

import java.util.Map;

/**
 * Holder that holds {@link RegistryOverrideConfiguration} and {@link SearchConfiguration} for specific entity.
 */
@Getter
@Builder
public class RegistryOverrideConfigurationHolder {

    /**
     * Entity type.
     */
    private final Class<?> type;

    /**
     * Registry override configuration.
     */
    private final RegistryOverrideConfiguration overrideConfiguration;

    /**
     * Search override configuration.
     */
    private final SearchConfiguration<Object, Object, Map<String, Object>> overrideSearchConfiguration;

}
