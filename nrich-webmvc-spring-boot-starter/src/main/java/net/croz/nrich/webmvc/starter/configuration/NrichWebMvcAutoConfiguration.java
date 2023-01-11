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
import net.croz.nrich.notification.api.service.BaseNotificationResponseService;
import net.croz.nrich.springboot.condition.ConditionalOnPropertyNotEmpty;
import net.croz.nrich.webmvc.advice.ControllerEditorRegistrationAdvice;
import net.croz.nrich.webmvc.advice.NotificationErrorHandlingRestControllerAdvice;
import net.croz.nrich.webmvc.api.service.ExceptionAuxiliaryDataResolverService;
import net.croz.nrich.webmvc.api.service.ExceptionHttpStatusResolverService;
import net.croz.nrich.webmvc.localeresolver.ConstrainedSessionLocaleResolver;
import net.croz.nrich.webmvc.service.DefaultExceptionAuxiliaryDataResolverService;
import net.croz.nrich.webmvc.service.DefaultTransientPropertyResolverService;
import net.croz.nrich.webmvc.service.MessageSourceExceptionHttpStatusResolverService;
import net.croz.nrich.webmvc.service.TransientPropertyResolverService;
import net.croz.nrich.webmvc.starter.properties.NrichWebMvcProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(NrichWebMvcProperties.class)
@Configuration(proxyBeanMethods = false)
public class NrichWebMvcAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public TransientPropertyResolverService transientPropertyResolverService() {
        return new DefaultTransientPropertyResolverService();
    }

    @ConditionalOnProperty(name = "nrich.webmvc.exception-auxiliary-data-resolving-enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    @Bean
    public ExceptionAuxiliaryDataResolverService exceptionAuxiliaryDataResolverService() {
        return new DefaultExceptionAuxiliaryDataResolverService();
    }

    @ConditionalOnMissingBean
    @Bean
    public ControllerEditorRegistrationAdvice controllerEditorRegistrationAdvice(NrichWebMvcProperties webMvcProperties, TransientPropertyResolverService transientPropertyResolverService) {
        return new ControllerEditorRegistrationAdvice(webMvcProperties.isConvertEmptyStringsToNull(), webMvcProperties.isIgnoreTransientFields(), transientPropertyResolverService);
    }

    @ConditionalOnMissingBean
    @Bean
    public ExceptionHttpStatusResolverService exceptionHttpStatusResolverService(MessageSource messageSource) {
        return new MessageSourceExceptionHttpStatusResolverService(messageSource);
    }

    @ConditionalOnProperty(name = "nrich.webmvc.controller-advice-enabled", havingValue = "true", matchIfMissing = true)
    @Bean
    public NotificationErrorHandlingRestControllerAdvice notificationRestControllerAdvice(NrichWebMvcProperties webMvcProperties, BaseNotificationResponseService<?> notificationResponseService,
                                                                                          LoggingService loggingService, ExceptionHttpStatusResolverService exceptionHttpStatusResolverService,
                                                                                          @Autowired(required = false) ExceptionAuxiliaryDataResolverService exceptionAuxiliaryDataResolverService) {
        return new NotificationErrorHandlingRestControllerAdvice(
            webMvcProperties.getExceptionToUnwrapList(), webMvcProperties.getExceptionAuxiliaryDataToIncludeInNotification(), notificationResponseService, loggingService,
            exceptionAuxiliaryDataResolverService, exceptionHttpStatusResolverService
        );
    }

    @ConditionalOnPropertyNotEmpty("nrich.webmvc.allowed-locale-list")
    @ConditionalOnMissingBean
    @Bean
    public ConstrainedSessionLocaleResolver constrainedSessionLocaleResolver(NrichWebMvcProperties webMvcProperties) {
        return new ConstrainedSessionLocaleResolver(webMvcProperties.getDefaultLocale(), webMvcProperties.getAllowedLocaleList());
    }
}
