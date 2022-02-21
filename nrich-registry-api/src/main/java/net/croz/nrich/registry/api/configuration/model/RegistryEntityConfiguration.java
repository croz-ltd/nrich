package net.croz.nrich.registry.api.configuration.model;

import lombok.Builder;
import lombok.Getter;
import net.croz.nrich.registry.api.configuration.model.property.RegistryPropertyConfiguration;

import java.util.List;

/**
 * Represents client entity configuration that can be used when building form and grids on client side.
 */
@Getter
@Builder
public class RegistryEntityConfiguration {

    /**
     * Fully qualified classname of this registry entity.
     */
    private final String classFullName;

    /**
     * Simple class name.
     */
    private final String name;

    /**
     * Name to be displayed.
     */
    private final String displayName;

    /**
     * Group to which this registry belongs to.
     */
    private final String groupId;

    /**
     * Whether this entity is read only.
     */
    private final boolean readOnly;

    /**
     * Whether new instances of this entity can be created.
     */
    private final boolean creatable;

    /**
     * Whether this entity can be updated.
     */
    private final boolean updateable;

    /**
     * Whether this entity can be deleted.
     */
    private final boolean deletable;

    /**
     * Whether this entity has identifier assigned.
     */
    private final boolean isIdentifierAssigned;

    /**
     * Whether this entity has {@link javax.persistence.IdClass} identifier.
     */
    private final boolean isIdClassIdentity;

    /**
     * Whether this entity has {@link javax.persistence.EmbeddedId} identifier.
     */
    private final boolean isEmbeddedIdentity;

    /**
     * List of property names from which the id consists of.
     */
    private final List<String> idClassPropertyNameList;

    /**
     * Whether history for this entity is available.
     */
    private final boolean isHistoryAvailable;

    /**
     * List of property configurations.
     */
    private final List<RegistryPropertyConfiguration> propertyConfigurationList;

    /**
     * List of embedded id property configurations.
     */
    private final List<RegistryPropertyConfiguration> embeddedIdPropertyConfigurationList;

    /**
     * List of history properties, only available if history exists.
     */
    private final List<RegistryPropertyConfiguration> historyPropertyConfigurationList;

}
