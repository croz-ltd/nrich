package net.croz.nrich.formconfiguration.service;

import net.croz.nrich.formconfiguration.api.model.ConstrainedProperty;

import java.util.Locale;

public interface FieldErrorMessageResolverService {

    String resolveErrorMessage(ConstrainedProperty request, Locale locale);

}
