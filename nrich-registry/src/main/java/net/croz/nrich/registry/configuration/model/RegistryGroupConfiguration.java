package net.croz.nrich.registry.configuration.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class RegistryGroupConfiguration {

    private final String registryGroupId;

    private final String registryGroupIdDisplay;

    private final List<RegistryEntityConfiguration> registryEntityConfigurationList;

    private final List<RegistryPropertyConfiguration> registryHistoryPropertyConfigurationList;

}
