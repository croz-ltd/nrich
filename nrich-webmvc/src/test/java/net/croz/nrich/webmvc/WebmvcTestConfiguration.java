package net.croz.nrich.webmvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.croz.nrich.logging.service.LoggingService;
import net.croz.nrich.logging.service.impl.LoggingServiceImpl;
import net.croz.nrich.notification.service.NotificationResolverService;
import net.croz.nrich.notification.service.impl.NotificationResolverServiceImpl;
import net.croz.nrich.webmvc.advice.ControllerEditorRegistrationAdvice;
import net.croz.nrich.webmvc.advice.NotificationErrorHandlingRestControllerAdvice;
import net.croz.nrich.webmvc.service.ExceptionAuxiliaryDataResolverService;
import net.croz.nrich.webmvc.service.TransientPropertyResolverService;
import net.croz.nrich.webmvc.service.impl.ExceptionAuxiliaryDataResolverServiceImpl;
import net.croz.nrich.webmvc.service.impl.TransientPropertyResolverServiceImpl;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
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
    public NotificationResolverService notificationResolverService(final MessageSource messageSource) {
        return new NotificationResolverServiceImpl(messageSource);
    }

    @Bean
    public LoggingService loggingService(final MessageSource messageSource) {
        return new LoggingServiceImpl(messageSource);
    }

    @Bean
    public TransientPropertyResolverService transientPropertyResolverService() {
       return new TransientPropertyResolverServiceImpl();
    }

    @Bean
    public ControllerEditorRegistrationAdvice controllerEditorRegistrationAdvice(final TransientPropertyResolverService transientPropertyResolverService) {
        return new ControllerEditorRegistrationAdvice(true, true, transientPropertyResolverService);
    }

    @Bean
    public ExceptionAuxiliaryDataResolverService exceptionAuxiliaryDataResolverService() {
        return new ExceptionAuxiliaryDataResolverServiceImpl();
    }

    @Bean
    public NotificationErrorHandlingRestControllerAdvice notificationErrorHandlingRestControllerAdvice(final NotificationResolverService notificationResolverService, final LoggingService loggingService, final ExceptionAuxiliaryDataResolverService exceptionAuxiliaryDataResolverService) {
        return new NotificationErrorHandlingRestControllerAdvice(Collections.singletonList(ExecutionException.class.getName()), Collections.singletonList("uuid"), notificationResolverService, loggingService, exceptionAuxiliaryDataResolverService);
    }
}