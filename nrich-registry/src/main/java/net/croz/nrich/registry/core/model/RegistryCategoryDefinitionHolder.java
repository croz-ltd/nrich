package net.croz.nrich.registry.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class RegistryCategoryDefinitionHolder {

   private final List<RegistryCategoryDefinition> registryCategoryDefinitionList;

   private final List<String> registryCategoryDisplayOrderList;

}
