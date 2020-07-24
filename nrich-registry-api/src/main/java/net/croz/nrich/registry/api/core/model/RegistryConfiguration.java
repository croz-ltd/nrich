package net.croz.nrich.registry.api.core.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * User defined configuration for registry entities.
 */
@Setter
@Getter
public class RegistryConfiguration {

    /**
     * Order in which groups should be sorted.
     */
    private List<String> groupDisplayOrderList;

    /**
     * List of {@link RegistryGroupDefinitionConfiguration} instances that hold registry configuration.
     */
    private List<RegistryGroupDefinitionConfiguration> groupDefinitionConfigurationList;

    /**
     * List of {@link RegistryOverrideConfigurationHolder} instances that hold override configuration for registry groups.
     */
    private List<RegistryOverrideConfigurationHolder> overrideConfigurationHolderList;

    /**
     * Order of history properties.
     */
    private List<String> historyDisplayOrderList;

}
