package net.croz.nrich.webmvc.starter.configuration;

import net.croz.nrich.logging.api.service.LoggingService;
import net.croz.nrich.logging.service.Slf4jLoggingService;
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

    @ConditionalOnMissingBean
    @Bean
    public LoggingService loggingService(MessageSource messageSource) {
        return new Slf4jLoggingService(messageSource);
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
