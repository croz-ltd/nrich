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

package net.croz.nrich.webmvc.starter.configuration;

import net.croz.nrich.logging.api.service.LoggingService;
import net.croz.nrich.logging.starter.configuration.NrichLoggingAutoConfiguration;
import net.croz.nrich.notification.starter.configuration.NrichNotificationAutoConfiguration;
import net.croz.nrich.webmvc.advice.ControllerEditorRegistrationAdvice;
import net.croz.nrich.webmvc.advice.NotificationErrorHandlingRestControllerAdvice;
import net.croz.nrich.webmvc.api.service.ExceptionAuxiliaryDataResolverService;
import net.croz.nrich.webmvc.api.service.ExceptionHttpStatusResolverService;
import net.croz.nrich.webmvc.localeresolver.ConstrainedSessionLocaleResolver;
import net.croz.nrich.webmvc.service.TransientPropertyResolverService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.support.ResourceBundleMessageSource;

import static org.assertj.core.api.Assertions.assertThat;

class NrichWebMvcAutoConfigurationTest {

    private final WebApplicationContextRunner webApplicationContextRunner = new WebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(NrichLoggingAutoConfiguration.class, NrichNotificationAutoConfiguration.class, NrichWebMvcAutoConfiguration.class));

    @Test
    void shouldConfigureDefaultConfiguration() {
        // expect
        webApplicationContextRunner.withBean(ResourceBundleMessageSource.class).run(context -> {
            assertThat(context).hasSingleBean(LoggingService.class);
            assertThat(context).hasSingleBean(TransientPropertyResolverService.class);
            assertThat(context).hasSingleBean(ExceptionAuxiliaryDataResolverService.class);
            assertThat(context).hasSingleBean(ControllerEditorRegistrationAdvice.class);
            assertThat(context).hasSingleBean(NotificationErrorHandlingRestControllerAdvice.class);
            assertThat(context).hasSingleBean(ExceptionHttpStatusResolverService.class);
            assertThat(context).doesNotHaveBean(ConstrainedSessionLocaleResolver.class);
        });
    }

    @Test
    void shouldAddConstrainedLocaleResolverWhenAllowedLocaleListIsNotEmpty() {
        // expect
        webApplicationContextRunner.withPropertyValues("nrich.webmvc.allowed-locale-list=hr,en").withBean(ResourceBundleMessageSource.class).run(context ->
            assertThat(context).hasSingleBean(ConstrainedSessionLocaleResolver.class)
        );
    }

    @Test
    void shouldNotAddAdviceWhenItsDisabled() {
        // expect
        webApplicationContextRunner.withPropertyValues("nrich.webmvc.controller-advice-enabled=false").withBean(ResourceBundleMessageSource.class).run(context ->
            assertThat(context).doesNotHaveBean(NotificationErrorHandlingRestControllerAdvice.class)
        );
    }
}
