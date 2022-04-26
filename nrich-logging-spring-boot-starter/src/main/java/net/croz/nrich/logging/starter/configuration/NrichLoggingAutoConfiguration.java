package net.croz.nrich.logging.starter.configuration;

import net.croz.nrich.logging.api.service.LoggingService;
import net.croz.nrich.logging.service.Slf4jLoggingService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnBean(MessageSource.class)
@Configuration(proxyBeanMethods = false)
public class NrichLoggingAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public LoggingService loggingService(MessageSource messageSource) {
        return new Slf4jLoggingService(messageSource);
    }
}
