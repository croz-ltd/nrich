package net.croz.nrich.registry.configuration.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RegistryGroupConfiguration {

    private final String registryGroupId;

    private final String registryGroupIdDisplay;

    private final List<RegistryEntityConfiguration> registryEntityConfigurationList;

}
