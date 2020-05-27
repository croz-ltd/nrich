package net.croz.nrich.registry.data.util;

import net.croz.nrich.registry.data.model.RegistryDataConfiguration;

import java.util.List;

public final class RegistrySearchConfigurationUtil {

    private RegistrySearchConfigurationUtil() {
    }

    public static void verifyConfigurationExists(final List<RegistryDataConfiguration<?, ?>> registryDataConfigurationList, final String classFullName) {
         registryDataConfigurationList.stream()
                .filter(configuration -> configuration.getRegistryType().getName().equals(classFullName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Configuration for registry entity %s is not defined!", classFullName)));
    }

    public static RegistryDataConfiguration<?, ?> findRegistryConfigurationForClass(final List<RegistryDataConfiguration<?, ?>> registryDataConfigurationList, final String classFullName) {
        return registryDataConfigurationList.stream()
                .filter(configuration -> configuration.getRegistryType().getName().equals(classFullName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Configuration for registry entity %s is not defined!", classFullName)));
    }
}
