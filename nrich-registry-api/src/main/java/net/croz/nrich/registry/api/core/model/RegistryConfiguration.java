package net.croz.nrich.registry.api.core.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class RegistryConfiguration {

    private List<String> registryCategoryDisplayOrderList;

    private List<RegistryCategoryDefinitionConfiguration> registryCategoryDefinitionConfigurationList;

    private List<RegistryOverrideConfigurationHolder> registryOverrideConfigurationHolderList;

    private List<String> registryHistoryDisplayList;

}
