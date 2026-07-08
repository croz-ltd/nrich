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
import net.croz.nrich.logging.constant.LoggingConstants;
import net.croz.nrich.logging.service.Slf4jLoggingService;
import net.croz.nrich.problemdetail.api.contributor.ProblemDetailContributor;
import net.croz.nrich.problemdetail.api.service.ValidationErrorResolvingService;
import net.croz.nrich.problemdetail.constant.ProblemDetailConstants;
import net.croz.nrich.problemdetail.contributor.CodeProblemDetailContributor;
import net.croz.nrich.problemdetail.contributor.ErrorIdProblemDetailContributor;
import net.croz.nrich.problemdetail.contributor.SeverityProblemDetailContributor;
import net.croz.nrich.problemdetail.contributor.TimestampProblemDetailContributor;
import net.croz.nrich.problemdetail.contributor.ValidationErrorsProblemDetailContributor;
import net.croz.nrich.problemdetail.handler.NrichProblemDetailExceptionHandler;
import net.croz.nrich.problemdetail.service.DefaultValidationErrorResolvingService;
import net.croz.nrich.problemdetail.starter.properties.NrichProblemDetailProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractResourceBasedMessageSource;

import java.util.List;

@EnableConfigurationProperties(NrichProblemDetailProperties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(name = "nrich.problem-detail.enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureBefore(name = "net.croz.nrich.logging.starter.configuration.NrichLoggingAutoConfiguration")
@Configuration(proxyBeanMethods = false)
public class NrichProblemDetailAutoConfiguration {

    @ConditionalOnProperty(name = "nrich.problem-detail.logging-service-registration-enabled", havingValue = "true", matchIfMissing = true)
    @Bean
    public LoggingService loggingService(MessageSource messageSource) {
        return new Slf4jLoggingService(
            messageSource,
            List.of(ProblemDetailConstants.LOGGING_LEVEL_CODE_FORMAT, LoggingConstants.LOGGING_LEVEL_RESOLVING_FORMAT),
            List.of(ProblemDetailConstants.LOGGING_VERBOSITY_CODE_FORMAT, LoggingConstants.LOGGING_VERBOSITY_LEVEL_RESOLVING_FORMAT)
        );
    }

    @ConditionalOnProperty(name = "nrich.problem-detail.register-messages", havingValue = "true", matchIfMissing = true)
    @Bean
    public ProblemDetailMessageSourceRegistrar problemDetailMessageSourceRegistrar(MessageSource messageSource) {
        return new ProblemDetailMessageSourceRegistrar(messageSource);
    }

    @ConditionalOnMissingBean
    @Bean
    public ValidationErrorResolvingService validationErrorResolvingService(MessageSource messageSource, NrichProblemDetailProperties properties) {
        return new DefaultValidationErrorResolvingService(messageSource, properties.includeRejectedValue());
    }

    @ConditionalOnProperty(prefix = "nrich.problem-detail.contributor", name = "errors", havingValue = "true", matchIfMissing = true)
    @Bean
    public ValidationErrorsProblemDetailContributor validationErrorsProblemDetailContributor(ValidationErrorResolvingService validationErrorResolvingService) {
        return new ValidationErrorsProblemDetailContributor(validationErrorResolvingService);
    }

    @ConditionalOnProperty(prefix = "nrich.problem-detail.contributor", name = "code", havingValue = "true", matchIfMissing = true)
    @Bean
    public CodeProblemDetailContributor codeProblemDetailContributor(MessageSource messageSource, NrichProblemDetailProperties properties) {
        return new CodeProblemDetailContributor(messageSource, properties.fallbackToClassName());
    }

    @ConditionalOnProperty(prefix = "nrich.problem-detail.contributor", name = "severity", havingValue = "true", matchIfMissing = true)
    @Bean
    public SeverityProblemDetailContributor severityProblemDetailContributor(MessageSource messageSource) {
        return new SeverityProblemDetailContributor(messageSource);
    }

    @ConditionalOnProperty(prefix = "nrich.problem-detail.contributor", name = "error-id", havingValue = "true", matchIfMissing = true)
    @Bean
    public ErrorIdProblemDetailContributor errorIdProblemDetailContributor() {
        return new ErrorIdProblemDetailContributor();
    }

    @ConditionalOnProperty(prefix = "nrich.problem-detail.contributor", name = "timestamp", havingValue = "true", matchIfMissing = true)
    @Bean
    public TimestampProblemDetailContributor timestampProblemDetailContributor() {
        return new TimestampProblemDetailContributor();
    }

    @ConditionalOnMissingBean
    @Bean
    public NrichProblemDetailExceptionHandler nrichProblemDetailExceptionHandler(MessageSource messageSource, LoggingService loggingService, List<ProblemDetailContributor> contributors,
                                                                                 NrichProblemDetailProperties properties) {
        return new NrichProblemDetailExceptionHandler(messageSource, loggingService, contributors, properties.exceptionToUnwrapList());
    }

    public static class ProblemDetailMessageSourceRegistrar implements InitializingBean {

        private final MessageSource messageSource;

        public ProblemDetailMessageSourceRegistrar(MessageSource messageSource) {
            this.messageSource = messageSource;
        }

        @Override
        public void afterPropertiesSet() {
            if (messageSource instanceof AbstractResourceBasedMessageSource resourceBasedMessageSource) {
                resourceBasedMessageSource.addBasenames(ProblemDetailConstants.MESSAGES_RESOURCE_BUNDLE);
            }
        }
    }

}
