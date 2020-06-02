package net.croz.nrich.registry.core.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class RegistryOverrideConfiguration {

    private boolean readOnly;

    private boolean deletable;

    private boolean isHistoryAvailable;

    private List<String> ignoredPropertyList;

    private List<String> propertyDisplayList;

    private List<String> nonEditablePropertyList;

    private List<String> nonSortablePropertyList;

}
