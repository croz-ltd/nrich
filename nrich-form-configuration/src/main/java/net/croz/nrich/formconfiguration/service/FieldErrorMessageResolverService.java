package net.croz.nrich.formconfiguration.service;

import net.croz.nrich.formconfiguration.model.ConstrainedProperty;

import java.util.Locale;

public interface FieldErrorMessageResolverService {

    String resolveErrorMessage(ConstrainedProperty request, Locale locale);

}
