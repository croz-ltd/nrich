package net.croz.nrich.registry.core.model;

import lombok.Getter;
import lombok.Setter;
import net.croz.nrich.search.model.SearchConfiguration;

import java.util.List;
import java.util.Map;

// TODO move to API
@Setter
@Getter
public class RegistryConfiguration {

    private Map<Class<?>, RegistryOverrideConfiguration> entityRegistryOverrideConfiguration;

    private List<String> registryGroupDisplayOrderList;

    private Map<Class<?>, SearchConfiguration<?, ?, Map<String, Object>>> entitySearchOverrideConfigurationMap;

    private List<RegistryGroupDefinitionConfiguration> registryGroupDefinitionConfigurationList;

}
