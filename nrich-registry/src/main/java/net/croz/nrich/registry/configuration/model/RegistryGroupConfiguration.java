package net.croz.nrich.registry.configuration.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RegistryGroupConfiguration {

    private final String registryGroupId;

    private final String registryGroupIdDisplay;

    private final List<RegistryConfiguration> registryConfigurationList;

}
