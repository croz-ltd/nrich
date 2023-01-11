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

package net.croz.nrich.spring.propertysource;

import net.croz.nrich.spring.propertysource.stub.ResourceWithoutDescription;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class YamlPropertySourceFactoryTest {

    private final YamlPropertySourceFactory yamlPropertySourceFactory = new YamlPropertySourceFactory();

    private final EncodedResource encodedResource = new EncodedResource(new ClassPathResource("yaml-property-source-factory-source.yml"));

    @Test
    void shouldLoadYamlResources() {
        // given
        String name = "customName";

        // when
        PropertySource<?> result = yamlPropertySourceFactory.createPropertySource(name, encodedResource);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getProperty("foo.first")).isEqualTo("value1");
        assertThat(result.getProperty("foo.second")).isEqualTo("value2");
    }

    @Test
    void shouldResolveNameForYamlResourceWhenNoneIsSpecified() {
        // given
        ResourceWithoutDescription resourceWithoutDescription = new ResourceWithoutDescription(new ByteArrayInputStream("foo: bar".getBytes(StandardCharsets.UTF_8)));

        // when
        PropertySource<?> firstResult = yamlPropertySourceFactory.createPropertySource(null, encodedResource);

        // then
        assertThat(firstResult.getName()).isEqualTo("class path resource [yaml-property-source-factory-source.yml]");

        // and when
        PropertySource<?> secondResult = yamlPropertySourceFactory.createPropertySource(null, new EncodedResource(resourceWithoutDescription));

        // then
        assertThat(secondResult.getName()).startsWith("ResourceWithoutDescription@");
    }

    @Test
    void shouldThrowFileNotFoundExceptionWhenResourceDoesntExist() {
        // given
        EncodedResource nonExistingResource = new EncodedResource(new ClassPathResource("non-existing-resource.yaml"));

        // when
        Throwable thrown = catchThrowable(() -> yamlPropertySourceFactory.createPropertySource("name", nonExistingResource));

        // then
        assertThat(thrown).isInstanceOf(FileNotFoundException.class);
    }
}
