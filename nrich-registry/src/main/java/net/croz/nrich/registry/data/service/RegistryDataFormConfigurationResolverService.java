package net.croz.nrich.registry.data.service;

import java.util.Map;

public interface RegistryDataFormConfigurationResolverService {

    Map<String, Class<?>> resolveRegistryFormConfiguration();

}
