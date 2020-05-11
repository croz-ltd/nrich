package net.croz.nrich.formconfiguration.model;

import lombok.Data;

@Data
public class ConstrainedPropertyClientValidatorConfiguration {

  private final Class<?> propertyType;

  private final String name;

  private final Object[] argumentList;

  private final String errorMessage;

}
