package net.croz.nrich.registry.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.registry.core.support.ManagedTypeWrapper;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class RegistryDataConfigurationHolder {

    private final Map<String, ManagedTypeWrapper> classNameManagedTypeWrapperMap;

    private final List<RegistryDataConfiguration<?, ?>> registryDataConfigurationList;

    public void verifyConfigurationExists(String classFullName) {
        findRegistryConfigurationForClass(classFullName);
    }

    public RegistryDataConfiguration<?, ?> findRegistryConfigurationForClass(String classFullName) {
        return registryDataConfigurationList.stream()
                .filter(configuration -> configuration.getRegistryType().getName().equals(classFullName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Configuration for registry entity %s is not defined!", classFullName)));
    }

    public ManagedTypeWrapper resolveManagedTypeWrapper(String className) {
        return classNameManagedTypeWrapperMap.get(className);
    }
}
