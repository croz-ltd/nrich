package net.croz.nrich.registry.api.configuration.service;

import net.croz.nrich.registry.api.configuration.model.RegistryCategoryConfiguration;

import java.util.List;

public interface RegistryConfigurationService {

    List<RegistryCategoryConfiguration> fetchRegistryCategoryConfigurationList();

}
