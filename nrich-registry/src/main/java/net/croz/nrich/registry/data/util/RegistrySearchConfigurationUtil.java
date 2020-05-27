package net.croz.nrich.registry.data.util;

import net.croz.nrich.registry.data.model.RegistrySearchConfiguration;

import java.util.List;

public final class RegistrySearchConfigurationUtil {

    private RegistrySearchConfigurationUtil() {
    }

    public static void verifyConfigurationExists(final List<RegistrySearchConfiguration<?, ?>> registrySearchConfigurationList, final String classFullName) {
         registrySearchConfigurationList.stream()
                .filter(configuration -> configuration.getRegistryType().getName().equals(classFullName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Configuration for registry entity %s is not defined!", classFullName)));
    }

    public static RegistrySearchConfiguration<?, ?> findRegistryConfigurationForClass(final List<RegistrySearchConfiguration<?, ?>> registrySearchConfigurationList, final String classFullName) {
        return registrySearchConfigurationList.stream()
                .filter(configuration -> configuration.getRegistryType().getName().equals(classFullName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Configuration for registry entity %s is not defined!", classFullName)));
    }
}
