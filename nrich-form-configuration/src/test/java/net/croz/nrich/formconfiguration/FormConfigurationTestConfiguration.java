/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package net.croz.nrich.formconfiguration;

import net.croz.nrich.formconfiguration.api.service.ConstrainedPropertyValidatorConverterService;
import net.croz.nrich.formconfiguration.api.service.FormConfigurationAnnotationResolvingService;
import net.croz.nrich.formconfiguration.api.service.FormConfigurationService;
import net.croz.nrich.formconfiguration.service.DefaultConstrainedPropertyValidatorConverterService;
import net.croz.nrich.formconfiguration.service.DefaultFormConfigurationAnnotationResolvingService;
import net.croz.nrich.formconfiguration.service.DefaultFormConfigurationService;
import net.croz.nrich.formconfiguration.service.FieldErrorMessageResolverService;
import net.croz.nrich.formconfiguration.service.MessageSourceFieldErrorMessageResolverService;
import net.croz.nrich.formconfiguration.stub.FormConfigurationServiceNestedIgnoredTestRequest;
import net.croz.nrich.formconfiguration.stub.FormConfigurationServiceNestedTestRequest;
import net.croz.nrich.formconfiguration.stub.FormConfigurationServiceTestRequest;
import net.croz.nrich.javascript.converter.DefaultJavaToJavascriptTypeConverter;
import net.croz.nrich.javascript.service.DefaultJavaToJavascriptTypeConversionService;
import net.croz.nrich.javascript.api.service.JavaToJavascriptTypeConversionService;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Collections;
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
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

        messageSource.setBasename("messages");

        return messageSource;
    }

    @Bean
    public FieldErrorMessageResolverService fieldErrorMessageResolverService(MessageSource messageSource) {
        return new MessageSourceFieldErrorMessageResolverService(messageSource);
    }

    @Bean
    public ConstrainedPropertyValidatorConverterService constrainedPropertyValidatorConverterService(FieldErrorMessageResolverService fieldErrorMessageResolverService) {
        return new DefaultConstrainedPropertyValidatorConverterService(fieldErrorMessageResolverService);
    }

    @Bean
    public FormConfigurationAnnotationResolvingService formConfigurationAnnotationResolvingService() {
        return new DefaultFormConfigurationAnnotationResolvingService();
    }

    @Bean
    public JavaToJavascriptTypeConversionService javaToJavascriptTypeConversionService() {
        return new DefaultJavaToJavascriptTypeConversionService(Collections.singletonList(new DefaultJavaToJavascriptTypeConverter()));
    }

    @Bean
    public FormConfigurationService formConfigurationService(LocalValidatorFactoryBean validator, List<ConstrainedPropertyValidatorConverterService> constrainedPropertyValidatorConverterServiceList,
                                                             FormConfigurationAnnotationResolvingService formConfigurationAnnotationResolvingService,
                                                             JavaToJavascriptTypeConversionService javaToJavascriptTypeConversionService) {
        Map<String, Class<?>> formIdConstraintHolderMap = new LinkedHashMap<>(formConfigurationAnnotationResolvingService.resolveFormConfigurations(Collections.singletonList("net.croz")));

        formIdConstraintHolderMap.put(SIMPLE_FORM_CONFIGURATION_FORM_ID, FormConfigurationServiceTestRequest.class);
        formIdConstraintHolderMap.put(NESTED_FORM_CONFIGURATION_FORM_ID, FormConfigurationServiceNestedTestRequest.class);
        formIdConstraintHolderMap.put(NESTED_FORM_NOT_VALIDATED_CONFIGURATION_FORM_ID, FormConfigurationServiceNestedIgnoredTestRequest.class);

        return new DefaultFormConfigurationService(validator.getValidator(), formIdConstraintHolderMap, constrainedPropertyValidatorConverterServiceList, javaToJavascriptTypeConversionService);
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }
}
