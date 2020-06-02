package net.croz.nrich.registry.configuration.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class RegistryGroupDefinition {

    private final String registryGroupId;

    private final List<Class<?>> registryEntityList;

}
