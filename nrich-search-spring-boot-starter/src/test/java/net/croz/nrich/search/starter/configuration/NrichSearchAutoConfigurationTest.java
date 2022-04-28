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

package net.croz.nrich.search.starter.configuration;

import net.croz.nrich.search.api.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.api.converter.StringToTypeConverter;
import net.croz.nrich.search.api.factory.RepositoryFactorySupportFactory;
import net.croz.nrich.search.starter.properties.NrichSearchProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class NrichSearchAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(NrichSearchAutoConfiguration.class));

    @Test
    void shouldConfigureDefaultConfiguration() {
        // expect
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(StringToTypeConverter.class);
            assertThat(context).hasSingleBean(StringToEntityPropertyMapConverter.class);
            assertThat(context).hasSingleBean(RepositoryFactorySupportFactory.class);
        });
    }

    @Test
    void shouldNotCreateDefaultValueConverterWhenCreationIsDisabled() {
        // expect
        contextRunner.withPropertyValues("nrich.search.default-converter-enabled=false").run(context ->
            assertThat(context).doesNotHaveBean(StringToTypeConverter.class)
        );
    }

    @Test
    void shouldAllowForOverridingStringSearchValues() {
        contextRunner.withPropertyValues("nrich.search.string-search.boolean-true-regex-pattern=new").run(context -> {
            // when
            NrichSearchProperties searchProperties = context.getBean(NrichSearchProperties.class);

            // then
            assertThat(searchProperties.getStringSearch()).isNotNull();
            assertThat(searchProperties.getStringSearch().getBooleanTrueRegexPattern()).isEqualTo("new");
            assertThat(searchProperties.getStringSearch().getBooleanFalseRegexPattern()).isEqualTo("^(?i)\\s*(false|no|ne)\\s*$");
        });
    }
}
