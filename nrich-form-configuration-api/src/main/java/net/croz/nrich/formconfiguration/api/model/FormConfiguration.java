package net.croz.nrich.formconfiguration.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Hold configuration for specific form id. Form id is registered through <pre>{@code Map<String, Class<?>> formIdConstraintHolderMap}</pre> map and maps received
 * form id from client to a class that holds constrained properties.
 */
@RequiredArgsConstructor
@Getter
public class FormConfiguration {

    /**
     * Registered form id for this form configuration.
     */
    private final String formId;

    /**
     * List of {@link ConstrainedPropertyConfiguration} instances holding property configuration for each property defined in the class that form id was mapped to.
     */
    private final List<ConstrainedPropertyConfiguration> constrainedPropertyConfigurationList;

}
