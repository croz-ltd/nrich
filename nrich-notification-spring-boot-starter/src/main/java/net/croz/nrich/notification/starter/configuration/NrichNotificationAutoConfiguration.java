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

package net.croz.nrich.notification.starter.configuration;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.notification.api.service.ConstraintConversionService;
import net.croz.nrich.notification.api.service.NotificationMessageResolverService;
import net.croz.nrich.notification.api.service.NotificationResolverService;
import net.croz.nrich.notification.api.service.BaseNotificationResponseService;
import net.croz.nrich.notification.api.service.NotificationResponseService;
import net.croz.nrich.notification.service.DefaultConstraintConversionService;
import net.croz.nrich.notification.service.DefaultNotificationResolverService;
import net.croz.nrich.notification.service.MessageSourceNotificationMessageResolverService;
import net.croz.nrich.notification.service.WebMvcNotificationResponseService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractResourceBasedMessageSource;

@Configuration(proxyBeanMethods = false)
public class NrichNotificationAutoConfiguration {

    public static final String NOTIFICATION_MESSAGES_NAME = "nrich-notification-messages";

    @ConditionalOnMissingBean
    @Bean
    public ConstraintConversionService constraintConversionService() {
        return new DefaultConstraintConversionService();
    }

    @ConditionalOnMissingBean
    @Bean
    public NotificationMessageResolverService notificationMessageResolverService(MessageSource messageSource) {
        return new MessageSourceNotificationMessageResolverService(messageSource);
    }

    @ConditionalOnMissingBean
    @Bean
    public NotificationResolverService notificationResolverService(NotificationMessageResolverService notificationMessageResolverService, ConstraintConversionService constraintConversionService) {
        return new DefaultNotificationResolverService(notificationMessageResolverService, constraintConversionService);
    }

    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnMissingBean(BaseNotificationResponseService.class)
    @Bean
    public NotificationResponseService notificationResponseService(NotificationResolverService notificationResolverService) {
        return new WebMvcNotificationResponseService(notificationResolverService);
    }

    @ConditionalOnProperty(name = "nrich.notification.register-messages", havingValue = "true", matchIfMissing = true)
    @Bean
    public NotificationMessageSourceRegistrar notificationMessageSourceRegistrar(MessageSource messageSource) {
        return new NotificationMessageSourceRegistrar(messageSource);
    }

    @RequiredArgsConstructor
    public static class NotificationMessageSourceRegistrar implements InitializingBean {

        private final MessageSource messageSource;

        @Override
        public void afterPropertiesSet() {
            if (messageSource instanceof AbstractResourceBasedMessageSource) {
                ((AbstractResourceBasedMessageSource) messageSource).addBasenames(NOTIFICATION_MESSAGES_NAME);
            }
        }
    }
}
