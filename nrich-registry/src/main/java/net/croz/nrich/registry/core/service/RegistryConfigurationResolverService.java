package net.croz.nrich.registry.core.service;

import net.croz.nrich.registry.api.core.model.RegistryOverrideConfiguration;
import net.croz.nrich.registry.core.model.RegistryDataConfigurationHolder;
import net.croz.nrich.registry.core.model.RegistryGroupDefinitionHolder;
import net.croz.nrich.registry.core.model.RegistryHistoryConfigurationHolder;

import java.util.Map;

public interface RegistryConfigurationResolverService {

    RegistryGroupDefinitionHolder resolveRegistryGroupDefinition();

    Map<Class<?>, RegistryOverrideConfiguration> resolveRegistryOverrideConfigurationMap();

    RegistryDataConfigurationHolder resolveRegistryDataConfiguration();

    RegistryHistoryConfigurationHolder resolveRegistryHistoryConfiguration();
}
