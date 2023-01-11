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

package net.croz.nrich.validation.starter.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.MessageSource;
import org.springframework.context.support.AbstractResourceBasedMessageSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class NrichValidationAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(NrichValidationAutoConfiguration.class));

    @Test
    void shouldConfigureDefaultConfiguration() {
        // expect
        contextRunner.run(context ->
            assertThat(context).hasSingleBean(NrichValidationAutoConfiguration.ValidationMessageSourceRegistrar.class)
        );
    }

    @Test
    void shouldNotRegisterValidationMessagesWhenDisabledViaProperty() {
        // expect
        contextRunner.withPropertyValues("nrich.validation.register-messages=false").run(context ->
            assertThat(context).doesNotHaveBean(NrichValidationAutoConfiguration.ValidationMessageSourceRegistrar.class)
        );
    }

    @Test
    void shouldRegisterMessagesWhenPossible() {
        // given
        AbstractResourceBasedMessageSource messageSource = mock(AbstractResourceBasedMessageSource.class);

        // expect
        contextRunner.withBean(MessageSource.class, () -> messageSource).run(context ->
            verify(messageSource).addBasenames(NrichValidationAutoConfiguration.VALIDATION_MESSAGES_NAME)
        );
    }
}
