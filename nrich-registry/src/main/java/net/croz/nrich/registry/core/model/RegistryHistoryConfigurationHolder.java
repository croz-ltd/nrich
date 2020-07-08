package net.croz.nrich.registry.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class RegistryHistoryConfigurationHolder {

    private final PropertyWithType revisionNumberProperty;

    private final PropertyWithType revisionTimestampProperty;

    private final PropertyWithType revisionTypeProperty;

    private final List<PropertyWithType> revisionAdditionalPropertyList;

    private final List<String> propertyDisplayList;

}
