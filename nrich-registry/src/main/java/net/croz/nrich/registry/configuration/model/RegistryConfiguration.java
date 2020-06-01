package net.croz.nrich.registry.configuration.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RegistryConfiguration {

    private final String registryId;

    private final String registryName;

    private final String registryDisplayName;

    private final String category;

    private final boolean readOnly;

    private final boolean insertable;

    private final boolean updateable;

    private final boolean deletable;

    private final boolean isIdentifierAssigned;

    private final boolean isCompositeIdentity;

    private final List<String> compositeIdentityPropertyNameList;

    private final boolean isHistoryAvailable;

    private final List<RegistryProperty> registryPropertyList;

}
