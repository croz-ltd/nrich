package net.croz.nrich.jackson.starter.configuration;

import com.fasterxml.jackson.databind.Module;
import net.croz.nrich.jackson.module.JacksonModuleUtil;
import net.croz.nrich.jackson.starter.properties.NrichJacksonProperties;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

// TODO this module is not really a starter think of a better name, maybe just remove starter suffix?
@PropertySource("classpath:nrich-jackson.properties")
@AutoConfigureAfter(JacksonAutoConfiguration.class)
@EnableConfigurationProperties(NrichJacksonProperties.class)
@Configuration(proxyBeanMethods = false)
public class NrichJacksonAutoConfiguration {

    @ConditionalOnProperty(name = "nrich.jackson.convert-empty-strings-to-null", havingValue = "true", matchIfMissing = true)
    @Bean
    public Module convertEmptyStringsToNullModule() {
        return JacksonModuleUtil.convertEmptyStringToNullModule();
    }

}
