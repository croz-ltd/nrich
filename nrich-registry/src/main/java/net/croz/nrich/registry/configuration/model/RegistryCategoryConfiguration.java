package net.croz.nrich.registry.configuration.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class RegistryCategoryConfiguration {

    private final String registryCategoryId;

    private final String registryCategoryIdDisplay;

    private final List<RegistryEntityConfiguration> registryEntityConfigurationList;

    private final List<RegistryPropertyConfiguration> registryHistoryPropertyConfigurationList;

}
