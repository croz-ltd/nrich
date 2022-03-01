package net.croz.nrich.registry.api.core.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

/**
 * Holder for configuration that overrides default values for each entity.
 */
@Setter
@Getter
@Builder
public class RegistryOverrideConfiguration {

    /**
     * Whether this entity is read only.
     */
    private boolean readOnly;

    /**
     * Whether new instances of this entity can be created.
     */
    private boolean creatable;

    /**
     * Whether this entity can be updated.
     */
    private boolean updateable;

    /**
     * Whether this entity can be deleted.
     */
    private boolean deletable;

    /**
     * Whether history for this entity is available.
     */
    private boolean isHistoryAvailable;

    /**
     * List of properties that should be excluded from configuration.
     */
    private List<String> ignoredPropertyList;

    /**
     * Order of properties.
     */
    private List<String> propertyDisplayOrderList;

    /**
     * List of non editable properties.
     */
    private List<String> nonEditablePropertyList;

    /**
     * List of non sortable properties.
     */
    private List<String> nonSortablePropertyList;

    /**
     * List of non searchable properties.
     */
    private List<String> nonSearchablePropertyList;

    public static RegistryOverrideConfiguration defaultConfiguration() {
        return RegistryOverrideConfiguration.builder()
            .readOnly(false)
            .creatable(true)
            .updateable(true)
            .deletable(true)
            .isHistoryAvailable(false)
            .ignoredPropertyList(Collections.emptyList())
            .propertyDisplayOrderList(Collections.emptyList())
            .nonEditablePropertyList(Collections.emptyList())
            .nonSortablePropertyList(Collections.emptyList())
            .nonSearchablePropertyList(Collections.emptyList())
            .build();
    }
}
