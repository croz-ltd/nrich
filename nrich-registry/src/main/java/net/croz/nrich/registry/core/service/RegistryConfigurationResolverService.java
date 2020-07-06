package net.croz.nrich.registry.core.service;

import net.croz.nrich.registry.api.model.RegistryOverrideConfiguration;
import net.croz.nrich.registry.core.model.RegistryDataConfigurationHolder;
import net.croz.nrich.registry.core.model.RegistryCategoryDefinitionHolder;
import net.croz.nrich.registry.core.model.RegistryHistoryConfigurationHolder;

import java.util.Map;

public interface RegistryConfigurationResolverService {

    RegistryCategoryDefinitionHolder resolveRegistryGroupDefinition();

    Map<Class<?>, RegistryOverrideConfiguration> resolveRegistryOverrideConfigurationMap();

    RegistryDataConfigurationHolder resolveRegistryDataConfiguration();

    RegistryHistoryConfigurationHolder resolveRegistryHistoryConfiguration();
}
