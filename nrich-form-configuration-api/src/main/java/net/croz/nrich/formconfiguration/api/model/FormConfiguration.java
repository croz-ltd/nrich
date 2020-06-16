package net.croz.nrich.formconfiguration.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class FormConfiguration {

    private final String formId;

    private final List<ConstrainedPropertyConfiguration> constrainedPropertyConfigurationList;

}
