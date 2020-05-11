package net.croz.nrich.formconfiguration.service.impl;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.formconfiguration.model.ConstrainedProperty;
import net.croz.nrich.formconfiguration.model.ConstrainedPropertyClientValidatorConfiguration;
import net.croz.nrich.formconfiguration.service.ConstrainedPropertyValidatorConverterService;
import net.croz.nrich.formconfiguration.service.FieldErrorMessageResolverService;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Order
public class DefaultConstrainedPropertyValidatorConverterService implements ConstrainedPropertyValidatorConverterService {

    private final FieldErrorMessageResolverService fieldErrorMessageResolverService;

    @Override
    public List<ConstrainedPropertyClientValidatorConfiguration> convert(final ConstrainedProperty constrainedProperty) {
        final String message = fieldErrorMessageResolverService.resolveErrorMessage(constrainedProperty, LocaleContextHolder.getLocale());
        final ConstrainedPropertyClientValidatorConfiguration validator = new ConstrainedPropertyClientValidatorConfiguration(constrainedProperty.getType(), constrainedProperty.getConstraintName(), constrainedProperty.getConstraintArgumentList(), message);

        return Collections.singletonList(validator);
    }

    @Override
    public boolean supports(final ConstrainedProperty constrainedProperty) {
        return true;
    }
}
