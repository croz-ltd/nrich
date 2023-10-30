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

package net.croz.nrich.validation.constraint.mapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.croz.nrich.validation.api.mapping.ConstraintValidatorRegistrar;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import jakarta.validation.Configuration;
import jakarta.validation.ConstraintValidator;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class DefaultConstraintValidatorRegistrar implements ConstraintValidatorRegistrar {

    private static final int INDEX_OF_CONSTRAINT_TYPE = 0;

    private final List<String> constraintPacakgeList;

    @Override
    public void registerConstraintValidators(Configuration<?> configuration) {
        if (configuration instanceof HibernateValidatorConfiguration hibernateValidatorConfiguration) {
            registerConstraintsInternal(hibernateValidatorConfiguration);
        }
        else {
            log.warn("Unable to register validation configuration, automatic registration is only supported for hibernate validator");
        }
    }

    protected <A extends Annotation> void registerConstraintsInternal(HibernateValidatorConfiguration configuration) {
        org.reflections.Configuration reflectionsConfiguration = new ConfigurationBuilder()
            .forPackages(constraintPacakgeList.toArray(new String[0]))
            .setScanners(Scanners.SubTypes);

        @SuppressWarnings("rawtypes")
        Set<Class<? extends ConstraintValidator>> constraintValidators = new Reflections(reflectionsConfiguration).getSubTypesOf(ConstraintValidator.class);

        constraintValidators.forEach(validatorClass -> {
            @SuppressWarnings("unchecked")
            Class<? extends ConstraintValidator<A, ?>> castedValidatorClass = (Class<? extends ConstraintValidator<A, ?>>) validatorClass;
            Class<A> annotationClass = annotationClass(validatorClass);

            registerConstraint(configuration, castedValidatorClass, annotationClass);
        });
    }

    @SuppressWarnings("unchecked")
    private <A extends Annotation> Class<A> annotationClass(Class<?> type) {
        ParameterizedType parameterizedType = (ParameterizedType) Arrays.stream(type.getGenericInterfaces())
            .filter(genericInterface -> ((ParameterizedType) genericInterface).getRawType().getTypeName().equals(ConstraintValidator.class.getName()))
            .findFirst()
            .orElseThrow();

        return (Class<A>) parameterizedType.getActualTypeArguments()[INDEX_OF_CONSTRAINT_TYPE];
    }

    private <A extends Annotation> void registerConstraint(HibernateValidatorConfiguration hibernateValidatorConfiguration, Class<? extends ConstraintValidator<A, ?>> validator, Class<A> annotationClass) {
        ConstraintMapping constraintMapping = hibernateValidatorConfiguration.createConstraintMapping();

        constraintMapping.constraintDefinition(annotationClass).validatedBy(validator);

        hibernateValidatorConfiguration.addMapping(constraintMapping);
    }
}
