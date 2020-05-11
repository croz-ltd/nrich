package net.croz.nrich.formconfiguration.service;

import net.croz.nrich.formconfiguration.model.ConstrainedProperty;
import net.croz.nrich.formconfiguration.model.ConstrainedPropertyClientValidatorConfiguration;

import java.util.List;

public interface ConstrainedPropertyValidatorConverterService {

    List<ConstrainedPropertyClientValidatorConfiguration> convert(ConstrainedProperty constrainedProperty);

    boolean supports(ConstrainedProperty constrainedProperty);

}
