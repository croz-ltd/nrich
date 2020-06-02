package net.croz.nrich.registry.configuration.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class RegistryGroupDefinitionHolder {

   private final List<RegistryGroupDefinition> registryGroupDefinitionList;

   private final List<String> registryGroupDisplayOrderList;

}
