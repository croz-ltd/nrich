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

package net.croz.nrich.webmvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.croz.nrich.logging.api.service.LoggingService;
import net.croz.nrich.logging.service.Slf4jLoggingService;
import net.croz.nrich.notification.api.service.BaseNotificationResponseService;
import net.croz.nrich.notification.api.service.ConstraintConversionService;
import net.croz.nrich.notification.api.service.NotificationMessageResolverService;
import net.croz.nrich.notification.api.service.NotificationResolverService;
import net.croz.nrich.notification.api.service.NotificationResponseService;
import net.croz.nrich.notification.service.DefaultConstraintConversionService;
import net.croz.nrich.notification.service.DefaultNotificationResolverService;
import net.croz.nrich.notification.service.MessageSourceNotificationMessageResolverService;
import net.croz.nrich.notification.service.WebMvcNotificationResponseService;
import net.croz.nrich.webmvc.advice.ControllerEditorRegistrationAdvice;
import net.croz.nrich.webmvc.advice.NotificationErrorHandlingRestControllerAdvice;
import net.croz.nrich.webmvc.api.service.ExceptionAuxiliaryDataResolverService;
import net.croz.nrich.webmvc.api.service.ExceptionHttpStatusResolverService;
import net.croz.nrich.webmvc.service.DefaultExceptionAuxiliaryDataResolverService;
import net.croz.nrich.webmvc.service.DefaultTransientPropertyResolverService;
import net.croz.nrich.webmvc.service.MessageSourceExceptionHttpStatusResolverService;
import net.croz.nrich.webmvc.service.TransientPropertyResolverService;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

@ComponentScan("net.croz.nrich.webmvc")
@EnableWebMvc
@Configuration(proxyBeanMethods = false)
public class WebmvcTestConfiguration {

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

        messageSource.setBasename("messages");

        return messageSource;
    }

    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.findAndRegisterModules();

        return objectMapper;
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Bean
    public ConstraintConversionService constraintConversionService() {
        return new DefaultConstraintConversionService();
    }

    @Bean
    public NotificationMessageResolverService notificationMessageResolverService(MessageSource messageSource) {
        return new MessageSourceNotificationMessageResolverService(messageSource);
    }

    @Bean
    public NotificationResolverService notificationResolverService(NotificationMessageResolverService notificationMessageResolverService, ConstraintConversionService constraintConversionService) {
        return new DefaultNotificationResolverService(notificationMessageResolverService, constraintConversionService);
    }

    @Bean
    public NotificationResponseService notificationResponseService(NotificationResolverService notificationResolverService) {
        return new WebMvcNotificationResponseService(notificationResolverService);
    }

    @Bean
    public LoggingService loggingService(MessageSource messageSource) {
        return new Slf4jLoggingService(messageSource);
    }

    @Bean
    public TransientPropertyResolverService transientPropertyResolverService() {
        return new DefaultTransientPropertyResolverService();
    }

    @Bean
    public ControllerEditorRegistrationAdvice controllerEditorRegistrationAdvice(TransientPropertyResolverService transientPropertyResolverService) {
        return new ControllerEditorRegistrationAdvice(true, true, transientPropertyResolverService);
    }

    @Bean
    public ExceptionAuxiliaryDataResolverService exceptionAuxiliaryDataResolverService() {
        return new DefaultExceptionAuxiliaryDataResolverService();
    }

    @Bean
    public ExceptionHttpStatusResolverService exceptionHttpStatusResolverService(MessageSource messageSource) {
        return new MessageSourceExceptionHttpStatusResolverService(messageSource);
    }

    @Bean
    public NotificationErrorHandlingRestControllerAdvice notificationErrorHandlingRestControllerAdvice(BaseNotificationResponseService<?> notificationResponseService, LoggingService loggingService,
                                                                                                       ExceptionAuxiliaryDataResolverService exceptionAuxiliaryDataResolverService,
                                                                                                       ExceptionHttpStatusResolverService exceptionHttpStatusResolverService) {
        return new NotificationErrorHandlingRestControllerAdvice(
            Collections.singletonList(ExecutionException.class.getName()), Collections.singletonList("uuid"), notificationResponseService,
            loggingService, exceptionAuxiliaryDataResolverService, exceptionHttpStatusResolverService
        );
    }
}
