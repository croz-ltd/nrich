package net.croz.nrich.formconfiguration.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * Client validation configuration for single constrained property. A class property with multiple constraints will be resolved to a list of ConstrainedPropertyClientValidatorConfiguration instances.
 */
@RequiredArgsConstructor
@Getter
public class ConstrainedPropertyClientValidatorConfiguration {

    /**
     * Type of constrained property
     */
    private final Class<?> propertyType;

    /**
     * Constraint name (i.e. NotNull)
     */
    private final String name;

    /**
     * Constraint arguments as a map
     */
    private final Map<String, Object> argumentMap;

    /**
     * Error message that should be shown if validation fails
     */
    private final String errorMessage;

}
