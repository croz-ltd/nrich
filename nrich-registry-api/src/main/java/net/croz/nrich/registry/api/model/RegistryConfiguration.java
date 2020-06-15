package net.croz.nrich.registry.api.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

// TODO move to API
@Setter
@Getter
public class RegistryConfiguration {

    private List<String> registryGroupDisplayOrderList;

    private List<RegistryGroupDefinitionConfiguration> registryGroupDefinitionConfigurationList;

    private List<RegistryOverrideConfigurationHolder> registryOverrideConfigurationHolderList;

    private List<String> registryHistoryDisplayList;

}
