package net.croz.nrich.formconfiguration;

import net.croz.nrich.formconfiguration.api.ConstrainedPropertyValidatorConverterService;
import net.croz.nrich.formconfiguration.service.FieldErrorMessageResolverService;
import net.croz.nrich.formconfiguration.api.FormConfigurationService;
import net.croz.nrich.formconfiguration.service.impl.DefaultConstrainedPropertyValidatorConverterService;
import net.croz.nrich.formconfiguration.service.impl.FieldErrorMessageResolverServiceImpl;
import net.croz.nrich.formconfiguration.service.impl.FormConfigurationServiceImpl;
import net.croz.nrich.formconfiguration.stub.FormConfigurationServiceNestedIgnoredTestRequest;
import net.croz.nrich.formconfiguration.stub.FormConfigurationServiceNestedTestRequest;
import net.croz.nrich.formconfiguration.stub.FormConfigurationServiceTestRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
public class FormConfigurationTestConfiguration {

    public static final String SIMPLE_FORM_CONFIGURATION_FORM_ID = "simpleForm.formId";

    public static final String NESTED_FORM_CONFIGURATION_FORM_ID = "nestedForm.formId";

    public static final String NESTED_FORM_NOT_VALIDATED_CONFIGURATION_FORM_ID = "nestedFormNotValidated.formId";

    @Bean
    public MessageSource messageSource() {
        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

        messageSource.setBasename("messages");

        return messageSource;
    }

    @Bean
    public FieldErrorMessageResolverService fieldErrorMessageResolverService(final MessageSource messageSource) {
        return new FieldErrorMessageResolverServiceImpl(messageSource);
    }

    @Bean
    public ConstrainedPropertyValidatorConverterService constrainedPropertyValidatorConverterService(final FieldErrorMessageResolverService fieldErrorMessageResolverService) {
        return new DefaultConstrainedPropertyValidatorConverterService(fieldErrorMessageResolverService);
    }

    @Bean
    public FormConfigurationService formConfigurationService(final LocalValidatorFactoryBean validator, final List<ConstrainedPropertyValidatorConverterService> constrainedPropertyValidatorConverterServiceList) {
        final Map<String, Class<?>> formIdConstraintHolderMap = new LinkedHashMap<>();

        formIdConstraintHolderMap.put(SIMPLE_FORM_CONFIGURATION_FORM_ID, FormConfigurationServiceTestRequest.class);
        formIdConstraintHolderMap.put(NESTED_FORM_CONFIGURATION_FORM_ID, FormConfigurationServiceNestedTestRequest.class);
        formIdConstraintHolderMap.put(NESTED_FORM_NOT_VALIDATED_CONFIGURATION_FORM_ID, FormConfigurationServiceNestedIgnoredTestRequest.class);

        return new FormConfigurationServiceImpl(validator.getValidator(), formIdConstraintHolderMap, constrainedPropertyValidatorConverterServiceList);
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }
}
