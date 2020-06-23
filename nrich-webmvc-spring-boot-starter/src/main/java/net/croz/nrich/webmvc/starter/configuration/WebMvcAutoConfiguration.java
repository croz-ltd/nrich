package net.croz.nrich.webmvc.starter.configuration;

import net.croz.nrich.logging.api.service.LoggingService;
import net.croz.nrich.notification.api.service.NotificationResponseService;
import net.croz.nrich.webmvc.advice.ControllerEditorRegistrationAdvice;
import net.croz.nrich.webmvc.advice.NotificationErrorHandlingRestControllerAdvice;
import net.croz.nrich.webmvc.api.service.ExceptionAuxiliaryDataResolverService;
import net.croz.nrich.webmvc.service.DefaultExceptionAuxiliaryDataResolverService;
import net.croz.nrich.webmvc.service.DefaultTransientPropertyResolverService;
import net.croz.nrich.webmvc.service.TransientPropertyResolverService;
import net.croz.nrich.webmvc.starter.properties.WebMvcProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnBean({ NotificationResponseService.class, LoggingService.class, MessageSource.class })
@EnableConfigurationProperties(WebMvcProperties.class)
@Configuration(proxyBeanMethods = false)
public class WebMvcAutoConfiguration {

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

    @Bean
    public ControllerEditorRegistrationAdvice controllerEditorRegistrationAdvice(final WebMvcProperties webMvcProperties, final TransientPropertyResolverService transientPropertyResolverService) {
        return new ControllerEditorRegistrationAdvice(webMvcProperties.isConvertEmptyStringsToNull(), webMvcProperties.isIgnoreTransientFields(), transientPropertyResolverService);
    }

    @Bean
    public NotificationErrorHandlingRestControllerAdvice notificationErrorHandlingRestControllerAdvice(final WebMvcProperties webMvcProperties, final NotificationResponseService<?> notificationResponseService, final LoggingService loggingService, @Autowired(required = false) final ExceptionAuxiliaryDataResolverService exceptionAuxiliaryDataResolverService) {
        return new NotificationErrorHandlingRestControllerAdvice(webMvcProperties.getExceptionToUnwrapList(), webMvcProperties.getExceptionAuxiliaryDataToIncludeInNotification(), notificationResponseService, loggingService, exceptionAuxiliaryDataResolverService);
    }
}
