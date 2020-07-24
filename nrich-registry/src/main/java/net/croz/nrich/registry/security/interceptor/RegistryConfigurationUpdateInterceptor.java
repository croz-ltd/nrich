package net.croz.nrich.registry.security.interceptor;

import net.croz.nrich.registry.api.core.model.RegistryOverrideConfiguration;
import net.croz.nrich.registry.api.data.interceptor.BaseRegistryDataInterceptor;
import net.croz.nrich.registry.api.security.exception.RegistryUpdateNotAllowedException;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class RegistryConfigurationUpdateInterceptor extends BaseRegistryDataInterceptor {

    private final Map<String, RegistryOverrideConfiguration> registryOverrideConfigurationMap;

    public RegistryConfigurationUpdateInterceptor(final Map<Class<?>, RegistryOverrideConfiguration> registryOverrideConfigurationMap) {
        this.registryOverrideConfigurationMap = initializeRegistryOverrideConfigurationMap(registryOverrideConfigurationMap);
    }

    @Override
    public void beforeRegistryCreate(final String classFullName, final Object entityData) {
        final RegistryOverrideConfiguration registryOverrideConfiguration = resolveConfiguration(classFullName);

        verifyRegistryOperation(classFullName, registryOverrideConfiguration.isReadOnly() || !registryOverrideConfiguration.isCreatable());
    }

    @Override
    public void beforeRegistryUpdate(final String classFullName, final Object id, final Object entityData) {
        final RegistryOverrideConfiguration registryOverrideConfiguration = resolveConfiguration(classFullName);

        verifyRegistryOperation(classFullName, registryOverrideConfiguration.isReadOnly() || !registryOverrideConfiguration.isUpdateable());
    }

    @Override
    public void beforeRegistryDelete(final String classFullName, final Object id) {
        final RegistryOverrideConfiguration registryOverrideConfiguration = resolveConfiguration(classFullName);

        verifyRegistryOperation(classFullName, registryOverrideConfiguration.isReadOnly() || !registryOverrideConfiguration.isDeletable());
    }

    private RegistryOverrideConfiguration resolveConfiguration(final String classFullName) {
        if (registryOverrideConfigurationMap.get(classFullName) == null) {
            return RegistryOverrideConfiguration.defaultConfiguration();
        }

        return registryOverrideConfigurationMap.get(classFullName);
    }

    private Map<String, RegistryOverrideConfiguration> initializeRegistryOverrideConfigurationMap(final Map<Class<?>, RegistryOverrideConfiguration> registryOverrideConfigurationMap) {
        if (registryOverrideConfigurationMap == null) {
            return Collections.emptyMap();
        }

        return registryOverrideConfigurationMap.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().getName(), Map.Entry::getValue));
    }

    private void verifyRegistryOperation(final String registryClassName, final boolean isAllowed) {
        if (isAllowed) {
            throw new RegistryUpdateNotAllowedException(String.format("Trying to update registry: %s that is not updatable", registryClassName));
        }
    }
}
