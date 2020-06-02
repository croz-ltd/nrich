package net.croz.nrich.registry.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class RegistryDataConfigurationHolder {

    private final List<RegistryDataConfiguration<?, ?>> registryDataConfigurationList;

    public void verifyConfigurationExists(final String classFullName) {
        findRegistryConfigurationForClass(classFullName);
    }

    public RegistryDataConfiguration<?, ?> findRegistryConfigurationForClass(final String classFullName) {
        return registryDataConfigurationList.stream()
                .filter(configuration -> configuration.getRegistryType().getName().equals(classFullName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Configuration for registry entity %s is not defined!", classFullName)));
    }
}
