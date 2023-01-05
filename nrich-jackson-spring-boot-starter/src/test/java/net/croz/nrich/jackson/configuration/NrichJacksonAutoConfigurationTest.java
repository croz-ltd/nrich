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

package net.croz.nrich.jackson.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.croz.nrich.jackson.starter.configuration.NrichJacksonAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class NrichJacksonAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration.class, NrichJacksonAutoConfiguration.class));

    @Test
    void shouldConfigureDefaultConfiguration() {
        // expect
        contextRunner.run(context ->
            assertThat(context).hasBean("convertEmptyStringsToNullModule")
        );
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
            ObjectMapper objectMapper = context.getBean(ObjectMapper.class);

            // then
            assertThat(objectMapper.isEnabled(JsonParser.Feature.ALLOW_COMMENTS)).isTrue();
            assertThat(objectMapper.isEnabled(MapperFeature.PROPAGATE_TRANSIENT_MARKER)).isTrue();

            assertThat(objectMapper.getSerializationConfig().getDefaultPropertyInclusion().getValueInclusion()).isEqualTo(JsonInclude.Include.NON_NULL);
            assertThat(objectMapper.getSerializationConfig().getDefaultPropertyInclusion().getContentInclusion()).isEqualTo(JsonInclude.Include.NON_NULL);

            assertThat(objectMapper.getSerializationConfig().isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS)).isFalse();
            assertThat(objectMapper.getSerializationConfig().isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)).isFalse();
            assertThat(objectMapper.getSerializationConfig().isEnabled(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)).isFalse();

            assertThat(objectMapper.getDeserializationConfig().isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)).isFalse();
            assertThat(objectMapper.getDeserializationConfig().isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)).isTrue();
        });
    }

    @Test
    void shouldSupportStandardOverrideOfJacksonProperties() {
        contextRunner.withPropertyValues("spring.jackson.parser.ALLOW_COMMENTS=false").run(context -> {
            // when
            ObjectMapper objectMapper = context.getBean(ObjectMapper.class);

            // then
            assertThat(objectMapper.isEnabled(JsonParser.Feature.ALLOW_COMMENTS)).isFalse();
        });
    }
}
