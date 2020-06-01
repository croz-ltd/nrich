package net.croz.nrich.registry.configuration.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RegistryGroupDefinitionHolder {

   private final List<RegistryGroupDefinition> registryGroupDefinitionList;

   private final List<String> registryGroupDisplayOrderList;

}
