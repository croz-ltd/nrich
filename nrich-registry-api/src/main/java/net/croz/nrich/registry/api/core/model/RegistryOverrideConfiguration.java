package net.croz.nrich.registry.api.core.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Setter
@Getter
@Builder
public class RegistryOverrideConfiguration {

    private boolean readOnly;

    private boolean creatable;

    private boolean updateable;

    private boolean deletable;

    private boolean isHistoryAvailable;

    private List<String> ignoredPropertyList;

    private List<String> propertyDisplayOrderList;

    private List<String> nonEditablePropertyList;

    private List<String> nonSortablePropertyList;

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
