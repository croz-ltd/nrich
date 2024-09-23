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

package net.croz.nrich.registry.starter.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.croz.nrich.formconfiguration.api.customizer.FormConfigurationMappingCustomizer;
import net.croz.nrich.javascript.api.converter.JavaToJavascriptTypeConverter;
import net.croz.nrich.javascript.api.service.JavaToJavascriptTypeConversionService;
import net.croz.nrich.registry.api.configuration.service.RegistryConfigurationService;
import net.croz.nrich.registry.api.core.model.RegistryConfiguration;
import net.croz.nrich.registry.api.core.model.RegistryGroupDefinitionConfiguration;
import net.croz.nrich.registry.api.core.model.RegistryOverrideConfigurationHolder;
import net.croz.nrich.registry.api.data.service.RegistryDataService;
import net.croz.nrich.registry.api.enumdata.service.RegistryEnumService;
import net.croz.nrich.registry.api.history.service.RegistryHistoryService;
import net.croz.nrich.registry.configuration.controller.RegistryConfigurationController;
import net.croz.nrich.registry.core.service.RegistryConfigurationResolverService;
import net.croz.nrich.registry.data.controller.RegistryDataController;
import net.croz.nrich.registry.data.service.RegistryDataRequestConversionService;
import net.croz.nrich.registry.enumdata.controller.RegistryEnumController;
import net.croz.nrich.registry.history.controller.RegistryHistoryController;
import net.croz.nrich.registry.security.interceptor.RegistryConfigurationUpdateInterceptor;
import net.croz.nrich.registry.starter.properties.NrichRegistryProperties;
import net.croz.nrich.search.api.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.api.converter.StringToTypeConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NrichRegistryAutoConfigurationTest {

    private static final String[] REGISTRY_CONFIGURATION = {
        "nrich.registry.registry-configuration.group-definition-configuration-list[0].group-id=DATA",
        "nrich.registry.registry-configuration.group-definition-configuration-list[0].include-entity-pattern-list=^net.croz.nrich.registry.data.stub.*$"
    };

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(
        AutoConfigurations.of(DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, NrichRegistryAutoConfiguration.class)
    );

    private final WebApplicationContextRunner webContextRunner = new WebApplicationContextRunner().withConfiguration(
        AutoConfigurations.of(DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, NrichRegistryAutoConfiguration.class)
    );

    @Test
    void shouldNotRegisterConfigurationWhenNoPropertyValuesHaveBeenConfigured() {
        // expect
        contextRunner.withBean(LocalValidatorFactoryBean.class).run(context -> {
            assertThat(context).doesNotHaveBean("registryDataModelMapper");
            assertThat(context).doesNotHaveBean("registryBaseModelMapper");
            assertThat(context).doesNotHaveBean(RegistryConfigurationResolverService.class);
            assertThat(context).doesNotHaveBean(RegistryConfigurationUpdateInterceptor.class);
            assertThat(context).doesNotHaveBean(RegistryConfigurationService.class);
            assertThat(context).doesNotHaveBean(RegistryDataService.class);
            assertThat(context).doesNotHaveBean(RegistryDataRequestConversionService.class);
            assertThat(context).doesNotHaveBean(RegistryHistoryService.class);
            assertThat(context).doesNotHaveBean(RegistryEnumService.class);
            assertThat(context).doesNotHaveBean(FormConfigurationMappingCustomizer.class);
        });
    }

    @Test
    void shouldMapConfigurationProperties() {
        // given
        String[] properties = {
            "nrich.registry.default-read-only-property-list=id,version",
            "nrich.registry.registry-search.date-format-list=dd.mm.YYYY",
            "nrich.registry.registry-search.decimal-number-format-list=#0.00",
            "nrich.registry.registry-search.boolean-true-regex-pattern=^(?i)\\s*(true|yes)\\s*$",
            "nrich.registry.registry-search.boolean-false-regex-pattern=^(?i)\\s*(false|no)\\s*$",
            "nrich.registry.registry-configuration.group-display-order-list=DATA",
            "nrich.registry.registry-configuration.history-display-order-list=id,name",
            "nrich.registry.registry-configuration.group-definition-configuration-list[0].group-id=DATA",
            "nrich.registry.registry-configuration.group-definition-configuration-list[0].include-entity-pattern-list=^net.croz.nrich.registry.data.stub.*$",
            "nrich.registry.registry-configuration.group-definition-configuration-list[0].exclude-entity-pattern-list=^net.croz.nrich.registry.data.stub.exclude.*$",
            "nrich.registry.registry-configuration.create-registry-class-mapping.test=java.lang.String",
            "nrich.registry.registry-configuration.update-registry-class-mapping.test=java.lang.String"
        };

        contextRunner.withPropertyValues(properties).withBean(LocalValidatorFactoryBean.class).run(context -> {
            // when
            NrichRegistryProperties registryProperties = context.getBean(NrichRegistryProperties.class);

            // then
            assertThat(registryProperties.defaultReadOnlyPropertyList()).containsExactly("id", "version");

            assertThat(registryProperties.registrySearch()).isNotNull();

            NrichRegistryProperties.RegistrySearchProperties registrySearchProperties = registryProperties.registrySearch();

            assertThat(registrySearchProperties.dateFormatList()).containsExactly("dd.mm.YYYY");
            assertThat(registrySearchProperties.decimalNumberFormatList()).containsExactly("#0.00");
            assertThat(registrySearchProperties.booleanTrueRegexPattern()).isEqualTo("^(?i)\\s*(true|yes)\\s*$");
            assertThat(registrySearchProperties.booleanFalseRegexPattern()).isEqualTo("^(?i)\\s*(false|no)\\s*$");

            assertThat(registryProperties.registryConfiguration()).isNotNull();

            RegistryConfiguration registryConfiguration = registryProperties.registryConfiguration();

            assertThat(registryConfiguration.getGroupDisplayOrderList()).containsExactly("DATA");
            assertThat(registryConfiguration.getHistoryDisplayOrderList()).containsExactly("id", "name");

            List<RegistryGroupDefinitionConfiguration> registryGroupDefinitionList = registryProperties.registryConfiguration().getGroupDefinitionConfigurationList();

            assertThat(registryGroupDefinitionList).extracting("groupId").containsExactly("DATA");
            assertThat(registryGroupDefinitionList).flatExtracting("includeEntityPatternList").containsExactly("^net.croz.nrich.registry.data.stub.*$");
            assertThat(registryGroupDefinitionList).flatExtracting("excludeEntityPatternList").containsExactly("^net.croz.nrich.registry.data.stub.exclude.*$");

            assertThat(registryConfiguration.getCreateRegistryClassMapping()).containsEntry("test", String.class);
            assertThat(registryConfiguration.getUpdateRegistryClassMapping()).containsEntry("test", String.class);
        });
    }

    @Test
    void shouldConfigureDefaultConfiguration() {
        // expect
        contextRunner.withPropertyValues(REGISTRY_CONFIGURATION).withBean(LocalValidatorFactoryBean.class).run(context -> {
            assertThat(context).hasBean("registryDataModelMapper");
            assertThat(context).hasBean("registryBaseModelMapper");

            assertThat(context).hasSingleBean(ObjectMapper.class);
            assertThat(context).hasSingleBean(StringToTypeConverter.class);
            assertThat(context).hasSingleBean(StringToEntityPropertyMapConverter.class);
            assertThat(context).hasSingleBean(RegistryConfigurationResolverService.class);
            assertThat(context).hasSingleBean(RegistryConfigurationUpdateInterceptor.class);
            assertThat(context).hasSingleBean(RegistryConfigurationService.class);
            assertThat(context).hasSingleBean(RegistryDataService.class);
            assertThat(context).hasSingleBean(RegistryDataRequestConversionService.class);
            assertThat(context).hasSingleBean(RegistryHistoryService.class);
            assertThat(context).hasSingleBean(FormConfigurationMappingCustomizer.class);
            assertThat(context).hasSingleBean(JavaToJavascriptTypeConversionService.class);
            assertThat(context).hasSingleBean(RegistryEnumService.class);
            assertThat(context).hasSingleBean(NrichRegistryProperties.class);
            assertThat(context.getBean(NrichRegistryProperties.class).registrySearch()).isNotNull();

            assertThat(context).doesNotHaveBean(RegistryConfigurationController.class);
            assertThat(context).doesNotHaveBean(RegistryDataController.class);
            assertThat(context).doesNotHaveBean(RegistryHistoryController.class);
            assertThat(context).doesNotHaveBean(RegistryEnumController.class);
        });
    }

    @Test
    void shouldRegisterControllersInWebEnvironment() {
        // expect
        webContextRunner.withPropertyValues(REGISTRY_CONFIGURATION).withBean(LocalValidatorFactoryBean.class).run(context -> {
            assertThat(context).hasSingleBean(RegistryConfigurationController.class);
            assertThat(context).hasSingleBean(RegistryDataController.class);
            assertThat(context).hasSingleBean(RegistryHistoryController.class);
            assertThat(context).hasSingleBean(RegistryEnumController.class);
        });
    }

    @Test
    void shouldNotCreateDefaultValueConverterWhenCreationIsDisabled() {
        contextRunner.withPropertyValues(REGISTRY_CONFIGURATION).withBean(LocalValidatorFactoryBean.class).withPropertyValues("nrich.registry.default-converter-enabled=false").run(context ->
            assertThat(context).doesNotHaveBean(StringToTypeConverter.class)
        );
    }

    @Test
    void shouldNotCreateDefaultJavaToJavascriptConverterWhenCreationIsDisabled() {
        // expect
        contextRunner.withPropertyValues(REGISTRY_CONFIGURATION).withBean(LocalValidatorFactoryBean.class)
            .withPropertyValues("nrich.registry.default-java-to-javascript-converter-enabled=false").run(context ->
                assertThat(context).doesNotHaveBean(JavaToJavascriptTypeConverter.class)
            );
    }

    @Test
    void shouldRegisterRegistryOverrideConfiguration() {
        RegistryOverrideConfigurationHolder holder = RegistryOverrideConfigurationHolder.builder().build();

        contextRunner.withPropertyValues(REGISTRY_CONFIGURATION).withBean(RegistryOverrideConfigurationHolder.class, () -> holder).run(context -> {
            // when
            NrichRegistryProperties registryProperties = context.getBean(NrichRegistryProperties.class);

            // then
            assertThat(registryProperties.registryConfiguration().getOverrideConfigurationHolderList()).containsExactly(holder);
        });
    }
}
