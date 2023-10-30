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

package net.croz.nrich.validation;

import net.croz.nrich.validation.api.mapping.ConstraintValidatorRegistrar;
import net.croz.nrich.validation.constraint.mapping.DefaultConstraintValidatorRegistrar;
import net.croz.nrich.validation.constraint.stub.NullWhenTestService;
import net.croz.nrich.validation.constraint.stub.SpelValidationTestService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

@Configuration(proxyBeanMethods = false)
public class ValidationTestConfiguration {

    @Bean
    Validator validator(ConstraintValidatorRegistrar constraintValidatorRegistrar) {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();

        registerConstraintMappingContributors(localValidatorFactoryBean, constraintValidatorRegistrar);

        return localValidatorFactoryBean;
    }

    @Bean
    NullWhenTestService nullWhenTestService() {
        return new NullWhenTestService();
    }

    @Bean
    SpelValidationTestService spelValidationTestService() {
        return new SpelValidationTestService();
    }

    @Bean
    ConstraintValidatorRegistrar constraintMappingRegistrar() {
        return new DefaultConstraintValidatorRegistrar(List.of("net.croz.nrich.validation.constraint.validator"));
    }

    private void registerConstraintMappingContributors(LocalValidatorFactoryBean validator, ConstraintValidatorRegistrar constraintValidatorRegistrar) {
        validator.setConfigurationInitializer(constraintValidatorRegistrar::registerConstraintValidators);
    }
}
