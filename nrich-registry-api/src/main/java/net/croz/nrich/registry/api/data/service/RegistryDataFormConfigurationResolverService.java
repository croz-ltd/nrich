package net.croz.nrich.registry.api.data.service;

import java.util.Map;

public interface RegistryDataFormConfigurationResolverService {

    Map<String, Class<?>> resolveRegistryFormConfiguration();

}
