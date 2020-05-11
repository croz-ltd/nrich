package net.croz.nrich.formconfiguration.model;

import lombok.Data;

import java.util.List;

@Data
public class FormConfiguration {

    private final String formId;

    private final List<ConstrainedPropertyConfiguration> constrainedPropertyConfigurationList;

}
