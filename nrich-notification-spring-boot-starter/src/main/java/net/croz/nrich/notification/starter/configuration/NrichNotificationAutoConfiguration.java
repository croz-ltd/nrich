package net.croz.nrich.notification.starter.configuration;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.notification.api.service.NotificationResolverService;
import net.croz.nrich.notification.api.service.NotificationResponseService;
import net.croz.nrich.notification.service.ConstraintConversionService;
import net.croz.nrich.notification.service.DefaultConstraintConversionService;
import net.croz.nrich.notification.service.DefaultNotificationResolverService;
import net.croz.nrich.notification.service.WebMvcNotificationResponseService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractResourceBasedMessageSource;

import javax.annotation.PostConstruct;

@Configuration(proxyBeanMethods = false)
public class NrichNotificationAutoConfiguration {

    private static final String NOTIFICATION_MESSAGES_NAME = "notificationMessages";

    @ConditionalOnMissingBean
    @Bean
    public ConstraintConversionService constraintConversionService() {
        return new DefaultConstraintConversionService();
    }

    @ConditionalOnMissingBean
    @Bean
    public NotificationResolverService notificationResolverService(final MessageSource messageSource, final ConstraintConversionService constraintConversionService) {
        return new DefaultNotificationResolverService(messageSource, constraintConversionService);
    }

    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @Bean
    public NotificationResponseService<?> notificationResponseService(final NotificationResolverService notificationResolverService) {
        return new WebMvcNotificationResponseService(notificationResolverService);
    }

    @ConditionalOnProperty(name = "nrich.notification.register-messages", havingValue = "true", matchIfMissing = true)
    @Bean
    public NotificationMessageSourceRegistrar notificationMessageSourceRegistrar(final MessageSource messageSource) {
        return new NotificationMessageSourceRegistrar(messageSource);
    }

    @RequiredArgsConstructor
    public static class NotificationMessageSourceRegistrar {

        private final MessageSource messageSource;

        @PostConstruct
        void registerNotificationMessages() {
            if (messageSource instanceof AbstractResourceBasedMessageSource) {
                ((AbstractResourceBasedMessageSource) messageSource).addBasenames(NOTIFICATION_MESSAGES_NAME);
            }
        }
    }
}
