package net.croz.nrich.registry.configuration.service;

import net.croz.nrich.registry.configuration.model.RegistryGroupConfiguration;

import java.util.List;

public interface RegistryConfigurationService {

    List<RegistryGroupConfiguration> readRegistryGroupConfigurationList();

}
