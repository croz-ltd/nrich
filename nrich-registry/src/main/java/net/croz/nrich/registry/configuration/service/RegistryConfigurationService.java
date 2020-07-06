package net.croz.nrich.registry.configuration.service;

import net.croz.nrich.registry.configuration.model.RegistryCategoryConfiguration;

import java.util.List;

public interface RegistryConfigurationService {

    List<RegistryCategoryConfiguration> fetchRegistryCategoryConfigurationList();

}
