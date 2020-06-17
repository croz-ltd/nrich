package net.croz.nrich.webmvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.croz.nrich.logging.api.service.LoggingService;
import net.croz.nrich.logging.service.Slf4jLoggingService;
import net.croz.nrich.notification.api.service.NotificationResolverService;
import net.croz.nrich.notification.api.service.NotificationResponseService;
import net.croz.nrich.notification.service.ConstraintConversionService;
import net.croz.nrich.notification.service.DefaultConstraintConversionService;
import net.croz.nrich.notification.service.DefaultNotificationResolverService;
import net.croz.nrich.notification.service.WebMvcNotificationResponseService;
import net.croz.nrich.webmvc.advice.ControllerEditorRegistrationAdvice;
import net.croz.nrich.webmvc.advice.NotificationErrorHandlingRestControllerAdvice;
import net.croz.nrich.webmvc.api.service.ExceptionAuxiliaryDataResolverService;
import net.croz.nrich.webmvc.service.DefaultExceptionAuxiliaryDataResolverService;
import net.croz.nrich.webmvc.service.DefaultTransientPropertyResolverService;
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
        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

        messageSource.setBasename("messages");

        return messageSource;
    }

    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();

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
    public NotificationResolverService notificationResolverService(final MessageSource messageSource, final ConstraintConversionService constraintConversionService) {
        return new DefaultNotificationResolverService(messageSource, constraintConversionService);
    }

    @Bean
    public NotificationResponseService<?> notificationResponseService(final NotificationResolverService notificationResolverService) {
        return new WebMvcNotificationResponseService(notificationResolverService);
    }

    @Bean
    public LoggingService loggingService(final MessageSource messageSource) {
        return new Slf4jLoggingService(messageSource);
    }

    @Bean
    public TransientPropertyResolverService transientPropertyResolverService() {
       return new DefaultTransientPropertyResolverService();
    }

    @Bean
    public ControllerEditorRegistrationAdvice controllerEditorRegistrationAdvice(final TransientPropertyResolverService transientPropertyResolverService) {
        return new ControllerEditorRegistrationAdvice(true, true, transientPropertyResolverService);
    }

    @Bean
    public ExceptionAuxiliaryDataResolverService exceptionAuxiliaryDataResolverService() {
        return new DefaultExceptionAuxiliaryDataResolverService();
    }

    @Bean
    public NotificationErrorHandlingRestControllerAdvice notificationErrorHandlingRestControllerAdvice(final NotificationResponseService<?> notificationResponseService, final LoggingService loggingService, final ExceptionAuxiliaryDataResolverService exceptionAuxiliaryDataResolverService) {
        return new NotificationErrorHandlingRestControllerAdvice(Collections.singletonList(ExecutionException.class.getName()), Collections.singletonList("uuid"), notificationResponseService, loggingService, exceptionAuxiliaryDataResolverService);
    }
}
