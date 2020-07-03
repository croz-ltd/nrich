package net.croz.nrich.validation.starter.configuration;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.validation.constraint.validator.ValidFileValidatorProperties;
import net.croz.nrich.validation.starter.properties.NrichValidationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractResourceBasedMessageSource;

import javax.annotation.PostConstruct;

@EnableConfigurationProperties(NrichValidationProperties.class)
@Configuration(proxyBeanMethods = false)
public class NrichValidationAutoConfiguration {

    private static final String VALIDATION_MESSAGES_NAME = "validationMessages";

    @Bean
    public ValidFileValidatorProperties validFileValidatorProperties(final NrichValidationProperties nrichValidationProperties) {
        return nrichValidationProperties.getFileValidation();
    }

    @ConditionalOnProperty(name = "nrich.validation.register-messages", havingValue = "true", matchIfMissing = true)
    @Bean
    public ValidationMessageSourceRegistrar validationMessageSourceRegistrar(final MessageSource messageSource) {
        return new ValidationMessageSourceRegistrar(messageSource);
    }

    @RequiredArgsConstructor
    public static class ValidationMessageSourceRegistrar {

        private final MessageSource messageSource;

        @PostConstruct
        void registerValidationMessages() {
            if (messageSource instanceof AbstractResourceBasedMessageSource) {
                ((AbstractResourceBasedMessageSource) messageSource).addBasenames(VALIDATION_MESSAGES_NAME);
            }
        }
    }
}
