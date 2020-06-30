package net.croz.nrich.formconfiguration.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
@Getter
public class ConstrainedPropertyClientValidatorConfiguration {

  private final Class<?> propertyType;

  private final String name;

  private final Map<String, Object> argumentMap;

  private final String errorMessage;

}
