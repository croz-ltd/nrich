/*
 *  Copyright 2020-2022 CROZ d.o.o, the original author or authors.
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
import net.croz.nrich.formconfiguration.api.model.FormConfiguration;
import net.croz.nrich.formconfiguration.api.service.ConstrainedPropertyValidatorConverterService;
import net.croz.nrich.formconfiguration.api.service.FormConfigurationService;
import net.croz.nrich.formconfiguration.controller.FormConfigurationController;
import net.croz.nrich.formconfiguration.service.FieldErrorMessageResolverService;
import net.croz.nrich.formconfiguration.starter.configuration.stub.FormConfigurationTestRequest;
import net.croz.nrich.formconfiguration.starter.properties.NrichFormConfigurationProperties;
import net.croz.nrich.javascript.api.converter.JavaToJavascriptTypeConverter;
import net.croz.nrich.javascript.api.service.JavaToJavascriptTypeConversionService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class NrichFormConfigurationAutoConfigurationTest {

    private static final String ENTRY_FORMAT = "nrich.form-configuration.form-configuration-mapping.%s=%s";

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(NrichFormConfigurationAutoConfiguration.class));

    @Test
    void shouldConfigureDefaultConfiguration() {
        // expect
        contextRunner.withBean(LocalValidatorFactoryBean.class).run(context -> {
            assertThat(context).hasSingleBean(FieldErrorMessageResolverService.class);
            assertThat(context).hasSingleBean(FormConfigurationService.class);
            assertThat(context).hasSingleBean(FormConfigurationController.class);
            assertThat(context).hasSingleBean(JavaToJavascriptTypeConversionService.class);
        });
    }

    @Test
    void shouldNotConfigureWhenNoValidatorBeanIsPresent() {
        // expect
        contextRunner.run(context -> {
            assertThat(context).doesNotHaveBean(FieldErrorMessageResolverService.class);
            assertThat(context).doesNotHaveBean(FormConfigurationService.class);
            assertThat(context).doesNotHaveBean(FormConfigurationController.class);
        });
    }

    @Test
    void shouldNotCreateDefaultValueConverterWhenCreationIsDisabled() {
        // expect
        contextRunner.withBean(LocalValidatorFactoryBean.class).withPropertyValues("nrich.form-configuration.default-converter-enabled=false").run(context ->
            assertThat(context).doesNotHaveBean(ConstrainedPropertyValidatorConverterService.class)
        );
    }

    @Test
    void shouldNotCreateDefaultJavaToJavascriptConverterWhenCreationIsDisabled() {
        // expect
        contextRunner.withBean(LocalValidatorFactoryBean.class).withPropertyValues("nrich.form-configuration.default-java-to-javascript-converter-enabled=false").run(context ->
            assertThat(context).doesNotHaveBean(JavaToJavascriptTypeConverter.class)
        );
    }

    @Test
    void shouldRegisterFormConfigurationMapping() {
        // given
        String createKey = "create-form";
        String updateKey = "update-form";
        Class<?> requestType = FormConfigurationTestRequest.class;
        String[] propertyValues = new String[] { String.format(ENTRY_FORMAT, createKey, requestType.getName()), String.format(ENTRY_FORMAT, updateKey, requestType.getName()) };

        contextRunner.withBean(LocalValidatorFactoryBean.class).withPropertyValues(propertyValues).run(context -> {
            // when
            Map<String, Class<?>> formConfigurationMapping = context.getBean(NrichFormConfigurationProperties.class).getFormConfigurationMapping();

            // then
            assertThat(formConfigurationMapping).containsEntry(createKey, requestType).containsEntry(updateKey, requestType);
        });
    }

    @Test
    void shouldAllowForCustomizationOfFormConfigurationMapping() {
        // given
        String createKey = "create-form-customized";
        Class<?> requestType = FormConfigurationTestRequest.class;
        String[] propertyValues = new String[] { String.format(ENTRY_FORMAT, createKey, requestType.getName()) };
        Supplier<FormConfigurationMappingCustomizer> supplier = () -> formConfigurationMapping -> formConfigurationMapping.put(createKey, requestType);

        contextRunner.withBean(LocalValidatorFactoryBean.class).withBean(FormConfigurationMappingCustomizer.class, supplier).withPropertyValues(propertyValues).run(context -> {
            // when
            Map<String, Class<?>> formConfigurationMapping = context.getBean(NrichFormConfigurationProperties.class).getFormConfigurationMapping();

            // then
            assertThat(formConfigurationMapping).containsEntry(createKey, requestType);
        });
    }

    @Test
    void shouldRegisterFormConfigurationFromClasspathWhenBasePackageIsSet() {
        contextRunner.withBean(LocalValidatorFactoryBean.class).withPropertyValues("nrich.form-configuration.form-validation-configuration-classes-package-list=net.croz").run(context -> {
            // when
            FormConfigurationService formConfigurationService = context.getBean(FormConfigurationService.class);

            // and when
            List<FormConfiguration> result = formConfigurationService.fetchFormConfigurationList();

            // then
            assertThat(result).extracting("formId").containsExactly("annotatedForm.formId");
        });
    }
}
