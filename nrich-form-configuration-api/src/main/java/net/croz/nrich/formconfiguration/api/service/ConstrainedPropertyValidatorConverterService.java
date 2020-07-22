package net.croz.nrich.formconfiguration.api.service;

import net.croz.nrich.formconfiguration.api.model.ConstrainedProperty;
import net.croz.nrich.formconfiguration.api.model.ConstrainedPropertyClientValidatorConfiguration;

import java.util.List;

/**
 * Converts {@link ConstrainedProperty} instances to a list of client validator configurations ({@link ConstrainedPropertyClientValidatorConfiguration}).
 */
public interface ConstrainedPropertyValidatorConverterService {

    /**
     * Returns a list of {@link ConstrainedPropertyClientValidatorConfiguration} instances for single property with constraint.
     * A list is retuned because some constraint may produce multiple constraints on client side.
     *
     * @param constrainedProperty constrained property to convert
     * @return list of client side validator configuration instances
     */
    List<ConstrainedPropertyClientValidatorConfiguration> convert(ConstrainedProperty constrainedProperty);

    /**
     * Returns whether constrained property is supported for conversion
     *
     * @param constrainedProperty constrained property to check
     * @return whether constrained property is supported
     */
    boolean supports(ConstrainedProperty constrainedProperty);

}
