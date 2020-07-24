package net.croz.nrich.registry.api.core.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class RegistryConfiguration {

    private List<String> groupDisplayOrderList;

    private List<RegistryGroupDefinitionConfiguration> groupDefinitionConfigurationList;

    private List<RegistryOverrideConfigurationHolder> overrideConfigurationHolderList;

    private List<String> historyDisplayList;

}
