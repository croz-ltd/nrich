package net.croz.nrich.registry.api.data.service;

import java.util.Map;

/**
 * Resolves form configuration for registry entities.
 */
public interface RegistryDataFormConfigurationResolverService {

    /**
     * Returns form configuration map for registry entities. Key is form id and value is class that holds registry creation or update data.
     *
     * @return form configuration map
     */
    Map<String, Class<?>> resolveRegistryFormConfiguration();

}
