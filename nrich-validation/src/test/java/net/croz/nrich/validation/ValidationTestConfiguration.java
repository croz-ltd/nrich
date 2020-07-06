package net.croz.nrich.validation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration(proxyBeanMethods = false)
public class ValidationTestConfiguration {

    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }

}
