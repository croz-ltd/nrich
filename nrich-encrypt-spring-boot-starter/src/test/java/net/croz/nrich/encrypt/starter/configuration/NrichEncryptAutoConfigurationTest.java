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

package net.croz.nrich.encrypt.starter.configuration;

import net.croz.nrich.encrypt.api.service.DataEncryptionService;
import net.croz.nrich.encrypt.api.service.TextEncryptionService;
import net.croz.nrich.encrypt.aspect.EncryptDataAspect;
import net.croz.nrich.encrypt.starter.properties.NrichEncryptProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class NrichEncryptAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(NrichEncryptAutoConfiguration.class));

    @Test
    void shouldConfigureDefaultConfiguration() {
        // expect
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(TextEncryptionService.class);
            assertThat(context).hasSingleBean(DataEncryptionService.class);
            assertThat(context).hasSingleBean(EncryptDataAspect.class);
            assertThat(context).hasSingleBean(NrichEncryptProperties.class);
        });
    }

    @Test
    void shouldNotCreateAspectWhenCreationIsDisabled() {
        // expect
        contextRunner.withPropertyValues("nrich.encrypt.encrypt-aspect-enabled=false").run(context ->
            assertThat(context).doesNotHaveBean(EncryptDataAspect.class)
        );
    }

    @Test
    void shouldCreateAdvisorWhenEncryptConfigurationIsDefined() {
        // given
        String[] propertyValues = {
            "nrich.encrypt.encryption-configuration-list[0].method-to-encrypt-decrypt=methodToEncrypt",
            "nrich.encrypt.encryption-configuration-list[0].property-to-encrypt-decrypt-list=property",
            "nrich.encrypt.encryption-configuration-list[0].encryption-operation=ENCRYPT"
        };

        // expect
        contextRunner.withPropertyValues(propertyValues).run(context ->
            assertThat(context).hasBean("encryptAdvisor")
        );
    }

    @CsvSource({ "'',''", "ec49c585b7c44f2b,''", "'',ec49c585b7c44f2b" })
    @ParameterizedTest
    void shouldCreateTextEncryptionServiceWithProvidedOrDefaultPasswordAndSalt(String password, String salt) {
        // given
        String[] propertyValues = {
            "nrich.encrypt.encrypt-password=" + password,
            "nrich.encrypt.encrypt-salt=" + salt
        };

        // expect
        contextRunner.withPropertyValues(propertyValues).run(context ->
            assertThat(context).hasSingleBean(TextEncryptionService.class)
        );
    }
}
