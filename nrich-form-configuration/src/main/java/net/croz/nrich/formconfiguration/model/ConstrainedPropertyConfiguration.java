package net.croz.nrich.formconfiguration.model;

import lombok.Data;

import java.util.List;

@Data
public class ConstrainedPropertyConfiguration {

    private final String path;

    private final List<ConstrainedPropertyClientValidatorConfiguration> validatorList;

}
