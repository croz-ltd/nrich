package net.croz.nrich.notification;

import net.croz.nrich.notification.api.service.NotificationResolverService;
import net.croz.nrich.notification.service.ConstraintConversionService;
import net.croz.nrich.notification.service.impl.ConstraintConversionServiceImpl;
import net.croz.nrich.notification.service.impl.NotificationResolverServiceImpl;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration(proxyBeanMethods = false)
public class NotificationTestConfiguration {

    @Bean
    public MessageSource messageSource() {
        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

        messageSource.setBasename("messages");

        return messageSource;
    }

    @Bean
    public ConstraintConversionService constraintConversionService() {
        return new ConstraintConversionServiceImpl();
    }

    @Bean
    public NotificationResolverService notificationResolverService(final MessageSource messageSource, final ConstraintConversionService constraintConversionService) {
        return new NotificationResolverServiceImpl(messageSource, constraintConversionService);
    }

    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }

}
