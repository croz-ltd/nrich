package net.croz.nrich.validation;

import net.croz.nrich.validation.constraint.validator.ValidFileValidatorProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Collections;

@Configuration(proxyBeanMethods = false)
public class ValidationTestConfiguration {

    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public ValidFileValidatorProperties validFileValidatorProperties() {
        final ValidFileValidatorProperties validFileValidatorProperties = new ValidFileValidatorProperties();
        validFileValidatorProperties.setAllowedExtensionList(Collections.singletonList("txt"));
        validFileValidatorProperties.setAllowedContentTypeList(Collections.singletonList("text/plain"));
        validFileValidatorProperties.setAllowedFileNameRegex("(?U)[\\w-.]+");

        return validFileValidatorProperties;
    }

}
