package net.croz.nrich.formconfiguration.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class ConstrainedPropertyConfiguration {

    private final String path;

    private final List<ConstrainedPropertyClientValidatorConfiguration> validatorList;

}
