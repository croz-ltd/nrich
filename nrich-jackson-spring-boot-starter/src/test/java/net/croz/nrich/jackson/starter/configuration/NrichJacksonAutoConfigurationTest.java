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

package net.croz.nrich.jackson.starter.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import net.croz.nrich.jackson.starter.properties.NrichJacksonProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class NrichJacksonAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration.class, NrichJacksonAutoConfiguration.class));

    @Test
    void shouldConfigureDefaultConfiguration() {
        // expect
        contextRunner.run(context -> {
            assertThat(context).hasBean("convertEmptyStringsToNullModule");
            assertThat(context).hasSingleBean(NrichJacksonProperties.class);
        });
    }

    @Test
    void shouldNotCreateDefaultValueConverterWhenCreationIsDisabled() {
        // expect
        contextRunner.withPropertyValues("nrich.jackson.convert-empty-strings-to-null=false").run(context ->
            assertThat(context).doesNotHaveBean("convertEmptyStringsToNullModule")
        );
    }

    @Test
    void shouldConfigureJacksonProperties() {
        contextRunner.run(context -> {
            // when
            JsonMapper jsonMapper = context.getBean(JsonMapper.class);

            // then
            assertThat(jsonMapper.isEnabled(JsonReadFeature.ALLOW_JAVA_COMMENTS)).isTrue();
            assertThat(jsonMapper.isEnabled(MapperFeature.PROPAGATE_TRANSIENT_MARKER)).isTrue();

            JsonInclude.Value inclusion = jsonMapper.serializationConfig().getDefaultPropertyInclusion();
            assertThat(inclusion.getValueInclusion()).isEqualTo(JsonInclude.Include.NON_NULL);
            assertThat(inclusion.getContentInclusion()).isEqualTo(JsonInclude.Include.NON_NULL);

            assertThat(jsonMapper.isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS)).isFalse();
            assertThat(jsonMapper.isEnabled(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)).isFalse();
            assertThat(jsonMapper.isEnabled(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)).isFalse();

            assertThat(jsonMapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)).isFalse();
            assertThat(jsonMapper.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)).isTrue();
        });
    }

    @Test
    void shouldSupportStandardOverrideOfJacksonProperties() {
        contextRunner.withPropertyValues("spring.jackson.json.read.ALLOW_JAVA_COMMENTS=false").run(context -> {
            // when
            JsonMapper jsonMapper = context.getBean(JsonMapper.class);

            // then
            assertThat(jsonMapper.isEnabled(JsonReadFeature.ALLOW_JAVA_COMMENTS)).isFalse();
        });
    }
}
