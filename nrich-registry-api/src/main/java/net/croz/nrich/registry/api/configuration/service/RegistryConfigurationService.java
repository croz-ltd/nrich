package net.croz.nrich.registry.api.configuration.service;

import net.croz.nrich.registry.api.configuration.model.RegistryGroupConfiguration;

import java.util.List;

/**
 * Resolves a list of {@link RegistryGroupConfiguration} instances. Data that will be returned and order is configured through
 * {@link net.croz.nrich.registry.api.core.model.RegistryConfiguration}.
 */
public interface RegistryConfigurationService {

    /**
     * Returns a list of {@link RegistryGroupConfiguration} instances.
     *
     * @return a list of {@link RegistryGroupConfiguration} instances.
     */
    List<RegistryGroupConfiguration> fetchRegistryGroupConfigurationList();

}
