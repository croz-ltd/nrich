package net.croz.nrich.registry.core.service;

import net.croz.nrich.registry.core.model.RegistryDataConfigurationHolder;
import net.croz.nrich.registry.core.model.RegistryGroupDefinitionHolder;
import net.croz.nrich.registry.core.model.RegistryOverrideConfiguration;

import java.util.Map;

public interface RegistryConfigurationResolverService {

    RegistryGroupDefinitionHolder resolveRegistryGroupDefinition();

    Map<Class<?>, RegistryOverrideConfiguration> resolveRegistryOverrideConfigurationMap();

    RegistryDataConfigurationHolder resolveRegistryDataConfiguration();

}
