package net.croz.nrich.validation.starter.configuration;

import net.croz.nrich.validation.constraint.validator.ValidFileValidatorProperties;
import net.croz.nrich.validation.starter.properties.NrichValidationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(NrichValidationProperties.class)
@Configuration(proxyBeanMethods = false)
public class NrichValidationAutoConfiguration {

    @Bean
    public ValidFileValidatorProperties validFileValidatorProperties(final NrichValidationProperties nrichValidationProperties) {
        return nrichValidationProperties.getFileValidation();
    }
}
