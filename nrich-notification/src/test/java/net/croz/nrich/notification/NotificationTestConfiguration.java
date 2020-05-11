package net.croz.nrich.notification;

import net.croz.nrich.notification.service.NotificationResolverService;
import net.croz.nrich.notification.service.impl.NotificationResolverServiceImpl;
import net.croz.nrich.notification.stub.NotificationAwareControllerTestComponent;
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
    public NotificationResolverService notificationResolverService(final MessageSource messageSource) {
        return new NotificationResolverServiceImpl(messageSource);
    }

    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public NotificationAwareControllerTestComponent notificationAwareControllerTestComponent(final NotificationResolverService notificationResolverService) {
        return new NotificationAwareControllerTestComponent(notificationResolverService);
    }
}
