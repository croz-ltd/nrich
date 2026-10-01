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

package net.croz.nrich.problemdetail;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.croz.nrich.logging.api.service.LoggingService;
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
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.mock;

@ComponentScan("net.croz.nrich.problemdetail")
@EnableWebMvc
@Configuration(proxyBeanMethods = false)
public class ProblemDetailTestConfiguration {

    @Bean
    MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

        messageSource.setBasenames("messages", ProblemDetailConstants.MESSAGES_RESOURCE_BUNDLE);

        return messageSource;
    }

    @Bean
    Validator validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.findAndRegisterModules();

        return objectMapper;
    }

    @Bean
    MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Bean
    LoggingService loggingService() {
        return mock(LoggingService.class);
    }

    @Bean
    ValidationErrorResolvingService validationErrorResolvingService(MessageSource messageSource) {
        return new DefaultValidationErrorResolvingService(messageSource, true);
    }

    @Bean
    ValidationErrorsProblemDetailContributor validationErrorsProblemDetailContributor(ValidationErrorResolvingService validationErrorResolvingService) {
        return new ValidationErrorsProblemDetailContributor(validationErrorResolvingService);
    }

    @Bean
    CodeProblemDetailContributor codeProblemDetailContributor(MessageSource messageSource) {
        return new CodeProblemDetailContributor(messageSource, false);
    }

    @Bean
    SeverityProblemDetailContributor severityProblemDetailContributor(MessageSource messageSource) {
        return new SeverityProblemDetailContributor(messageSource);
    }

    @Bean
    ErrorIdProblemDetailContributor errorIdProblemDetailContributor() {
        return new ErrorIdProblemDetailContributor();
    }

    @Bean
    TimestampProblemDetailContributor timestampProblemDetailContributor() {
        return new TimestampProblemDetailContributor();
    }

    @Bean
    NrichProblemDetailExceptionHandler nrichProblemDetailExceptionHandler(MessageSource messageSource, LoggingService loggingService, List<ProblemDetailContributor> contributors) {
        return new NrichProblemDetailExceptionHandler(messageSource, loggingService, contributors, List.of(ExecutionException.class.getName()));
    }
}
