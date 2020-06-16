package net.croz.nrich.formconfiguration.api;

import net.croz.nrich.formconfiguration.api.model.ConstrainedProperty;
import net.croz.nrich.formconfiguration.api.model.ConstrainedPropertyClientValidatorConfiguration;

import java.util.List;

public interface ConstrainedPropertyValidatorConverterService {

    List<ConstrainedPropertyClientValidatorConfiguration> convert(ConstrainedProperty constrainedProperty);

    boolean supports(ConstrainedProperty constrainedProperty);

}
