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

package net.croz.nrich.formconfiguration.starter.configuration;

import net.croz.nrich.formconfiguration.api.customizer.FormConfigurationMappingCustomizer;
import net.croz.nrich.formconfiguration.api.service.ConstrainedPropertyValidatorConverterService;
import net.croz.nrich.formconfiguration.api.service.FormConfigurationAnnotationResolvingService;
import net.croz.nrich.formconfiguration.api.service.FormConfigurationService;
import net.croz.nrich.formconfiguration.api.util.FormConfigurationMappingCustomizerUtil;
import net.croz.nrich.formconfiguration.controller.FormConfigurationController;
import net.croz.nrich.formconfiguration.service.DefaultConstrainedPropertyValidatorConverterService;
import net.croz.nrich.formconfiguration.service.DefaultFormConfigurationAnnotationResolvingService;
import net.croz.nrich.formconfiguration.service.DefaultFormConfigurationService;
import net.croz.nrich.formconfiguration.service.FieldErrorMessageResolverService;
import net.croz.nrich.formconfiguration.service.MessageSourceFieldErrorMessageResolverService;
import net.croz.nrich.formconfiguration.starter.properties.NrichFormConfigurationProperties;
import net.croz.nrich.javascript.api.converter.JavaToJavascriptTypeConverter;
import net.croz.nrich.javascript.api.service.JavaToJavascriptTypeConversionService;
import net.croz.nrich.javascript.converter.DefaultJavaToJavascriptTypeConverter;
import net.croz.nrich.javascript.service.DefaultJavaToJavascriptTypeConversionService;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.util.CollectionUtils;

import jakarta.validation.Validator;
import java.util.List;
import java.util.Map;

@AutoConfigureAfter(ValidationAutoConfiguration.class)
@ConditionalOnBean(Validator.class)
@EnableConfigurationProperties(NrichFormConfigurationProperties.class)
@Configuration(proxyBeanMethods = false)
public class NrichFormConfigurationAutoConfiguration {

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

    @ConditionalOnMissingBean
    @Bean
    public FormConfigurationAnnotationResolvingService formConfigurationAnnotationResolvingService() {
        return new DefaultFormConfigurationAnnotationResolvingService();
    }

    @ConditionalOnProperty(name = "nrich.form-configuration.default-java-to-javascript-converter-enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(name = "formJavaToJavascriptTypeConverter")
    @Bean
    public JavaToJavascriptTypeConverter formJavaToJavascriptTypeConverter() {
        return new DefaultJavaToJavascriptTypeConverter();
    }

    @ConditionalOnMissingBean(name = "formJavaToJavascriptTypeConversionService")
    @Bean
    public JavaToJavascriptTypeConversionService formJavaToJavascriptTypeConversionService(@Autowired(required = false) List<JavaToJavascriptTypeConverter> converters) {
        return new DefaultJavaToJavascriptTypeConversionService(converters);
    }

    @ConditionalOnMissingBean
    @Bean
    public FormConfigurationService formConfigurationService(@Lazy Validator validator, NrichFormConfigurationProperties configurationProperties,
                                                             List<ConstrainedPropertyValidatorConverterService> constrainedPropertyValidatorConverterServiceList,
                                                             FormConfigurationAnnotationResolvingService formConfigurationAnnotationResolvingService,
                                                             @Autowired(required = false) List<FormConfigurationMappingCustomizer> formConfigurationCustomizerList,
                                                             JavaToJavascriptTypeConversionService formJavaToJavascriptTypeConversionService) {
        Map<String, Class<?>> formConfigurationMapping = FormConfigurationMappingCustomizerUtil.applyCustomizerList(
            configurationProperties.formConfigurationMapping(), formConfigurationCustomizerList
        );

        List<String> packageList = configurationProperties.formValidationConfigurationClassesPackageList();
        if (!CollectionUtils.isEmpty(packageList)) {
            Map<String, Class<?>> classPathFormConfiguration = formConfigurationAnnotationResolvingService.resolveFormConfigurations(packageList);

            classPathFormConfiguration.forEach(formConfigurationMapping::putIfAbsent);
        }

        return new DefaultFormConfigurationService(validator, formConfigurationMapping, constrainedPropertyValidatorConverterServiceList, formJavaToJavascriptTypeConversionService);
    }

    @ConditionalOnMissingBean
    @Bean
    public FormConfigurationController formConfigurationController(FormConfigurationService formConfigurationService) {
        return new FormConfigurationController(formConfigurationService);
    }
}
