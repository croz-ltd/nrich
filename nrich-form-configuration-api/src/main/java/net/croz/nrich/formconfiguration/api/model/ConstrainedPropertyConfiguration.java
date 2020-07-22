package net.croz.nrich.formconfiguration.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Contains all {@link ConstrainedPropertyClientValidatorConfiguration} instances for a single property with path.
 */
@RequiredArgsConstructor
@Getter
public class ConstrainedPropertyConfiguration {

    /**
     * Path to the property relative to a parent class that is mapped to form id.
     */
    private final String path;

    /**
     * List of {@link ConstrainedPropertyClientValidatorConfiguration} instances that hold client side validation configuration.
     */
    private final List<ConstrainedPropertyClientValidatorConfiguration> validatorList;

}
