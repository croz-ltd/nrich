package net.croz.nrich.formconfiguration.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ConstrainedPropertyClientValidatorConfiguration {

  private final Class<?> propertyType;

  private final String name;

  private final Object[] argumentList;

  private final String errorMessage;

}
