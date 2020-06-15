package net.croz.nrich.registry.api.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class RegistryConfiguration {

    private List<String> registryGroupDisplayOrderList;

    private List<RegistryGroupDefinitionConfiguration> registryGroupDefinitionConfigurationList;

    private List<RegistryOverrideConfigurationHolder> registryOverrideConfigurationHolderList;

    private List<String> registryHistoryDisplayList;

}
