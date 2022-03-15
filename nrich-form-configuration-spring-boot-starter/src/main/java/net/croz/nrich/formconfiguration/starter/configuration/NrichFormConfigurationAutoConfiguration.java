package net.croz.nrich.formconfiguration.starter.configuration;

import net.croz.nrich.formconfiguration.api.service.ConstrainedPropertyValidatorConverterService;
import net.croz.nrich.formconfiguration.api.service.FormConfigurationService;
import net.croz.nrich.formconfiguration.controller.FormConfigurationController;
import net.croz.nrich.formconfiguration.service.DefaultConstrainedPropertyValidatorConverterService;
import net.croz.nrich.formconfiguration.service.DefaultFormConfigurationService;
import net.croz.nrich.formconfiguration.service.FieldErrorMessageResolverService;
import net.croz.nrich.formconfiguration.service.MessageSourceFieldErrorMessageResolverService;
import net.croz.nrich.formconfiguration.starter.properties.NrichFormConfigurationProperties;
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
@EnableConfigurationProperties(NrichFormConfigurationProperties.class)
@Configuration(proxyBeanMethods = false)
public class NrichFormConfigurationAutoConfiguration {

    public static final String MAPPING_BEAN_NAME = "formConfigurationMapping";

    @ConditionalOnMissingBean
    @Bean
    public FieldErrorMessageResolverService fieldErrorMessageResolverService(MessageSource messageSource) {
        return new MessageSourceFieldErrorMessageResolverService(messageSource);
    }

    @ConditionalOnProperty(name = "nrich.form-configuration.default-converter-enabled", havingValue = "true", matchIfMissing = true)
    @Bean
    public ConstrainedPropertyValidatorConverterService constrainedPropertyValidatorConverterService(FieldErrorMessageResolverService fieldErrorMessageResolverService) {
        return new DefaultConstrainedPropertyValidatorConverterService(fieldErrorMessageResolverService);
    }

    @ConditionalOnMissingBean(name = MAPPING_BEAN_NAME)
    @Bean
    public Map<String, Class<?>> formConfigurationMapping() {
        return new HashMap<>();
    }

    @ConditionalOnMissingBean
    @Bean
    public FormConfigurationService formConfigurationService(@Lazy LocalValidatorFactoryBean validator, @Qualifier(MAPPING_BEAN_NAME) Map<String, Class<?>> formConfigurationMapping,
                                                             List<ConstrainedPropertyValidatorConverterService> constrainedPropertyValidatorConverterServiceList) {
        return new DefaultFormConfigurationService(validator, formConfigurationMapping, constrainedPropertyValidatorConverterServiceList);
    }

    @ConditionalOnMissingBean
    @Bean
    public FormConfigurationController formConfigurationController(FormConfigurationService formConfigurationService) {
        return new FormConfigurationController(formConfigurationService);
    }
}
