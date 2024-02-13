/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package net.croz.nrich.validation.starter.configuration;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.validation.api.mapping.ConstraintValidatorRegistrar;
import net.croz.nrich.validation.constraint.mapping.DefaultConstraintValidatorRegistrar;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.autoconfigure.validation.ValidationConfigurationCustomizer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractResourceBasedMessageSource;

import jakarta.validation.Validator;
import java.util.List;

@Configuration(proxyBeanMethods = false)
public class NrichValidationAutoConfiguration {

    public static final String VALIDATION_MESSAGES_NAME = "nrich-validation-messages";

    @ConditionalOnProperty(name = "nrich.validation.register-messages", havingValue = "true", matchIfMissing = true)
    @Bean
    public ValidationMessageSourceRegistrar validationMessageSourceRegistrar(MessageSource messageSource) {
        return new ValidationMessageSourceRegistrar(messageSource);
    }

    @RequiredArgsConstructor
    public static class ValidationMessageSourceRegistrar implements InitializingBean {

        private final MessageSource messageSource;

        @Override
        public void afterPropertiesSet() {
            if (messageSource instanceof AbstractResourceBasedMessageSource abstractResourceBasedMessageSource) {
                abstractResourceBasedMessageSource.addBasenames(VALIDATION_MESSAGES_NAME);
            }
        }
    }

    @ConditionalOnProperty(name = "nrich.validation.register-constraint-validators", havingValue = "true", matchIfMissing = true)
    @Bean
    ConstraintValidatorRegistrar constraintMappingRegistrar(@Value("${nrich.validation.validator-package-list:net.croz.nrich.validation.constraint.validator}") List<String> validatorPackageList) {
        return new DefaultConstraintValidatorRegistrar(validatorPackageList);
    }

    @ConditionalOnProperty(name = "nrich.validation.register-constraint-validators", havingValue = "true", matchIfMissing = true)
    @Bean
    ValidationConfigurationCustomizer validationConfigurationCustomizer(ConstraintValidatorRegistrar constraintValidatorRegistrar) {
        return constraintValidatorRegistrar::registerConstraintValidators;
    }

    @ConditionalOnBean(Validator.class)
    @ConditionalOnProperty(name = "nrich.validation.register-constraint-validators", havingValue = "true", matchIfMissing = true)
    @Bean
    HibernatePropertiesCustomizer validationHibernatePropertiesCustomizer(Validator validator) {
        return hibernateProperties -> hibernateProperties.put("jakarta.persistence.validation.factory", validator);
    }
}
