package net.croz.nrich.validation.starter.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractResourceBasedMessageSource;

@Configuration(proxyBeanMethods = false)
public class NrichValidationAutoConfiguration {

    private static final String VALIDATION_MESSAGES_NAME = "validationMessages";

    @ConditionalOnProperty(name = "nrich.validation.register-messages", havingValue = "true", matchIfMissing = true)
    @Bean
    public ValidationMessageSourceRegistrar validationMessageSourceRegistrar(final MessageSource messageSource) {
        return new ValidationMessageSourceRegistrar(messageSource);
    }

    @RequiredArgsConstructor
    public static class ValidationMessageSourceRegistrar implements InitializingBean {

        private final MessageSource messageSource;

        @Override
        public void afterPropertiesSet() {
            if (messageSource instanceof AbstractResourceBasedMessageSource) {
                ((AbstractResourceBasedMessageSource) messageSource).addBasenames(VALIDATION_MESSAGES_NAME);
            }
        }
    }
}
