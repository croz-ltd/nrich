package net.croz.nrich.registry.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RegistryHistoryConfigurationHolder {

    private final PropertyWithType revisionNumberProperty;

    private final PropertyWithType revisionTimestampProperty;

    private final List<PropertyWithType> revisionAdditionalPropertyList;

    private final List<String> propertyDisplayList;

}
