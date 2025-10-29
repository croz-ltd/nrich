package net.croz.nrich.validation.starter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

/**
 * @param registerMessages             Whether default constraint messages should be registered
 * @param registerConstraintValidators Whether default validators should be registered
 * @param validatorPackageList         A list containing all the validator packages that will be registered automatically
 */
@EnableConfigurationProperties(NrichValidationProperties.class)
@ConfigurationProperties("nrich.validation")
public record NrichValidationProperties(@DefaultValue("true") boolean registerMessages, @DefaultValue("true") boolean registerConstraintValidators,
                                        @DefaultValue("net.croz.nrich.validation.constraint.validator") List<String> validatorPackageList) {

}
