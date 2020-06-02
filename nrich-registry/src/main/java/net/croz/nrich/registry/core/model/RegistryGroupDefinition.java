package net.croz.nrich.registry.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.metamodel.ManagedType;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class RegistryGroupDefinition {

    private final String registryGroupId;

    private final List<ManagedType<?>> registryEntityList;

}
