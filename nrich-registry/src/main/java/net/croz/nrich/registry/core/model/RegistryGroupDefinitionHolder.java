package net.croz.nrich.registry.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class RegistryGroupDefinitionHolder {

    private final List<RegistryGroupDefinition> groupDefinitionList;

    private final List<String> groupDisplayOrderList;

}
