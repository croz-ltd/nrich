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

package net.croz.nrich.problemdetail.starter.configuration;

import net.croz.nrich.logging.api.service.LoggingService;
import net.croz.nrich.logging.starter.configuration.NrichLoggingAutoConfiguration;
import net.croz.nrich.problemdetail.api.service.ValidationErrorResolvingService;
import net.croz.nrich.problemdetail.contributor.CodeProblemDetailContributor;
import net.croz.nrich.problemdetail.contributor.ErrorIdProblemDetailContributor;
import net.croz.nrich.problemdetail.contributor.SeverityProblemDetailContributor;
import net.croz.nrich.problemdetail.contributor.TimestampProblemDetailContributor;
import net.croz.nrich.problemdetail.contributor.ValidationErrorsProblemDetailContributor;
import net.croz.nrich.problemdetail.handler.NrichProblemDetailExceptionHandler;
import net.croz.nrich.problemdetail.starter.properties.NrichProblemDetailProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class NrichProblemDetailAutoConfigurationTest {

    private final WebApplicationContextRunner webApplicationContextRunner = new WebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(NrichLoggingAutoConfiguration.class, NrichProblemDetailAutoConfiguration.class));

    @Test
    void shouldConfigureDefaultConfiguration() {
        // expect
        webApplicationContextRunner.withBean("messageSource", ResourceBundleMessageSource.class).run(context -> {
            assertThat(context).hasSingleBean(LoggingService.class);
            assertThat(context).hasSingleBean(NrichProblemDetailExceptionHandler.class);
            assertThat(context).hasSingleBean(ValidationErrorResolvingService.class);
            assertThat(context).hasSingleBean(ValidationErrorsProblemDetailContributor.class);
            assertThat(context).hasSingleBean(CodeProblemDetailContributor.class);
            assertThat(context).hasSingleBean(SeverityProblemDetailContributor.class);
            assertThat(context).hasSingleBean(ErrorIdProblemDetailContributor.class);
            assertThat(context).hasSingleBean(TimestampProblemDetailContributor.class);
        });
    }

    @Test
    void shouldNotIncludeRejectedValueByDefault() {
        // expect
        webApplicationContextRunner.withBean("messageSource", ResourceBundleMessageSource.class).run(context ->
            assertThat(context.getBean(NrichProblemDetailProperties.class).includeRejectedValue()).isFalse()
        );
    }

    @Test
    void shouldFallBackToDefaultLoggingServiceWhenLoggingRegistrationDisabled() {
        // expect
        webApplicationContextRunner.withBean("messageSource", ResourceBundleMessageSource.class).withPropertyValues("nrich.problem-detail.logging-service-registration-enabled=false").run(context -> {
            assertThat(context).hasSingleBean(LoggingService.class);
            assertThat(context).hasSingleBean(NrichProblemDetailExceptionHandler.class);
        });
    }

    @Test
    void shouldRegisterProblemDetailMessagesBundle() {
        // expect
        webApplicationContextRunner.withBean("messageSource", ResourceBundleMessageSource.class).run(context -> {
            MessageSource messageSource = context.getBean(MessageSource.class);

            assertThat(messageSource.getMessage("problemDetail.org.springframework.web.bind.MethodArgumentNotValidException", null, Locale.ENGLISH)).isEqualTo("Validation failed.");
            assertThat(messageSource.getMessage("problemDetail.nrich.default-detail", null, Locale.ENGLISH)).isEqualTo("Error occurred.");
            assertThat(messageSource.getMessage("problemDetail.nrich.default-detail", null, Locale.forLanguageTag("hr"))).startsWith("Dogodila se");
        });
    }

    @Test
    void shouldNotRegisterMessagesBundleWhenDisabled() {
        // expect
        webApplicationContextRunner.withBean("messageSource", ResourceBundleMessageSource.class).withPropertyValues("nrich.problem-detail.register-messages=false").run(context ->
            assertThat(context).doesNotHaveBean(NrichProblemDetailAutoConfiguration.ProblemDetailMessageSourceRegistrar.class)
        );
    }

    @Test
    void shouldNotRegisterHandlerWhenExplicitlyDisabled() {
        // expect
        webApplicationContextRunner.withBean("messageSource", ResourceBundleMessageSource.class).withPropertyValues("nrich.problem-detail.enabled=false").run(context ->
            assertThat(context).doesNotHaveBean(NrichProblemDetailExceptionHandler.class)
        );
    }

    @Test
    void shouldNotRegisterIndividualContributorsWhenDisabled() {
        // expect
        webApplicationContextRunner.withBean("messageSource", ResourceBundleMessageSource.class).withPropertyValues(
            "nrich.problem-detail.contributor.errors=false",
            "nrich.problem-detail.contributor.code=false",
            "nrich.problem-detail.contributor.severity=false",
            "nrich.problem-detail.contributor.error-id=false",
            "nrich.problem-detail.contributor.timestamp=false"
        ).run(context -> {
            assertThat(context).doesNotHaveBean(ValidationErrorsProblemDetailContributor.class);
            assertThat(context).doesNotHaveBean(CodeProblemDetailContributor.class);
            assertThat(context).doesNotHaveBean(SeverityProblemDetailContributor.class);
            assertThat(context).doesNotHaveBean(ErrorIdProblemDetailContributor.class);
            assertThat(context).doesNotHaveBean(TimestampProblemDetailContributor.class);
            assertThat(context).hasSingleBean(NrichProblemDetailExceptionHandler.class);
        });
    }

}
