package net.croz.nrich.registry.api.configuration.model;

import lombok.Builder;
import lombok.Getter;
import net.croz.nrich.registry.api.configuration.model.property.RegistryPropertyConfiguration;

import java.util.List;

@Getter
@Builder
public class RegistryEntityConfiguration {

    private final String registryId;

    private final String registryName;

    private final String registryDisplayName;

    private final String category;

    private final boolean readOnly;

    private final boolean creatable;

    private final boolean updateable;

    private final boolean deletable;;

    private final boolean isIdentifierAssigned;

    private final boolean isIdClassIdentity;

    private final boolean isEmbeddedIdentity;

    private final List<String> idClassPropertyNameList;

    private final boolean isHistoryAvailable;

    private final List<RegistryPropertyConfiguration> registryPropertyConfigurationList;

    private final List<RegistryPropertyConfiguration> registryEmbeddedIdPropertyConfigurationList;

    private final List<RegistryPropertyConfiguration> registryHistoryPropertyConfigurationList;

}
