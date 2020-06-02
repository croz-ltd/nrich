package net.croz.nrich.registry.core.model;

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

    private boolean deletable;

    private boolean isHistoryAvailable;

    private List<String> ignoredPropertyList;

    private List<String> propertyDisplayList;

    private List<String> nonEditablePropertyList;

    private List<String> nonSortablePropertyList;

    public static RegistryOverrideConfiguration defaultConfiguration() {
        return RegistryOverrideConfiguration.builder()
                .readOnly(false)
                .deletable(true)
                .isHistoryAvailable(false)
                .ignoredPropertyList(Collections.emptyList())
                .propertyDisplayList(Collections.emptyList())
                .nonEditablePropertyList(Collections.emptyList())
                .nonSortablePropertyList(Collections.emptyList())
                .build();
    }
}
