package net.croz.nrich.registry.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.registry.core.support.ManagedTypeWrapper;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class RegistryCategoryDefinition {

    private final String registryGroupId;

    private final List<ManagedTypeWrapper> registryEntityList;

}
