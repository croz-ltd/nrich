package net.croz.nrich.formconfiguration.starter.configuration;

import net.croz.nrich.formconfiguration.api.ConstrainedPropertyValidatorConverterService;
import net.croz.nrich.formconfiguration.api.FormConfigurationService;
import net.croz.nrich.formconfiguration.controller.FormConfigurationController;
import net.croz.nrich.formconfiguration.service.DefaultConstrainedPropertyValidatorConverterService;
import net.croz.nrich.formconfiguration.service.DefaultFormConfigurationService;
import net.croz.nrich.formconfiguration.service.FieldErrorMessageResolverService;
import net.croz.nrich.formconfiguration.service.MessageSourceFieldErrorMessageResolverService;
import net.croz.nrich.formconfiguration.starter.properties.FormConfigurationProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoConfigureAfter(ValidationAutoConfiguration.class)
@ConditionalOnBean(LocalValidatorFactoryBean.class)
@EnableConfigurationProperties(FormConfigurationProperties.class)
@Configuration(proxyBeanMethods = false)
public class FormConfigurationAutoConfiguration {

    public static final String FORM_CONFIGURATION_MAPPING_BEAN_NAME = "formConfigurationMapping";

    @ConditionalOnMissingBean
    @Bean
    public FieldErrorMessageResolverService fieldErrorMessageResolverService(final MessageSource messageSource) {
        return new MessageSourceFieldErrorMessageResolverService(messageSource);
    }

    @ConditionalOnProperty(name = "nrich.form-configuration.configuration.default-converter-enabled", havingValue = "true", matchIfMissing = true)
    @Bean
    public ConstrainedPropertyValidatorConverterService constrainedPropertyValidatorConverterService(final FieldErrorMessageResolverService fieldErrorMessageResolverService) {
        return new DefaultConstrainedPropertyValidatorConverterService(fieldErrorMessageResolverService);
    }

    @ConditionalOnMissingBean(name = FORM_CONFIGURATION_MAPPING_BEAN_NAME)
    @Bean
    public Map<String, Class<?>> formConfigurationMapping() {
        return new HashMap<>();
    }

    @ConditionalOnMissingBean
    @Bean
    public FormConfigurationService formConfigurationService(@Lazy final LocalValidatorFactoryBean validator, @Qualifier(FORM_CONFIGURATION_MAPPING_BEAN_NAME) final Map<String, Class<?>> formConfigurationMapping, final List<ConstrainedPropertyValidatorConverterService> constrainedPropertyValidatorConverterServiceList) {
        return new DefaultFormConfigurationService(validator, formConfigurationMapping, constrainedPropertyValidatorConverterServiceList);
    }

    @Bean
    public FormConfigurationController formConfigurationController(final FormConfigurationService formConfigurationService) {
        return new FormConfigurationController(formConfigurationService);
    }
}
