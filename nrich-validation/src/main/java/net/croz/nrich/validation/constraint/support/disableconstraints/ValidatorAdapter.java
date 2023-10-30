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

package net.croz.nrich.validation.constraint.support.disableconstraints;

import lombok.RequiredArgsConstructor;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.executable.ExecutableValidator;
import jakarta.validation.metadata.BeanDescriptor;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Enables usage of {@link net.croz.nrich.validation.api.constraint.DisableConstraints} annotation for disabling validation constraints.
 */
@RequiredArgsConstructor
public class ValidatorAdapter implements Validator {

    private final DisableConstraintsAnnotationProcessor constraintAnnotationProcessor = new DisableConstraintsAnnotationProcessor();

    private final Validator targetValidator;

    @Override
    public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> constraintViolations = targetValidator.validate(object, groups);

        return filterConstraints(constraintViolations, object.getClass());
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
        Set<ConstraintViolation<T>> constraintViolations = targetValidator.validateProperty(object, propertyName, groups);

        return filterConstraints(constraintViolations, object.getClass());
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups) {
        Set<ConstraintViolation<T>> constraintViolations = targetValidator.validateValue(beanType, propertyName, value, groups);

        return filterConstraints(constraintViolations, beanType);
    }

    @Override
    public BeanDescriptor getConstraintsForClass(Class<?> type) {
        BeanDescriptor beanDescriptor = targetValidator.getConstraintsForClass(type);

        return new BeanDescriptorAdapter(beanDescriptor, constraintAnnotationProcessor.getDisabledConstraintForType(type));
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        return targetValidator.unwrap(type);
    }

    @Override
    public ExecutableValidator forExecutables() {
        return targetValidator.forExecutables();
    }

    private <T> Set<ConstraintViolation<T>> filterConstraints(Set<ConstraintViolation<T>> originalViolations, Class<?> type) {
        if (originalViolations.isEmpty()) {
            return originalViolations;
        }

        Map<String, List<Class<? extends Annotation>>> pathHolderMap = constraintAnnotationProcessor.getDisabledConstraintForType(type);

        if (pathHolderMap.isEmpty()) {
            return originalViolations;
        }

        return originalViolations.stream().filter(constraintViolation -> {
            Class<?> annotationType = constraintViolation.getConstraintDescriptor().getAnnotation().annotationType();
            Class<?> beanType = constraintViolation.getRootBeanClass();
            String propertyName = constraintViolation.getPropertyPath().toString();

            String path = PathUtil.getPath(beanType, propertyName);

            return !(pathHolderMap.getOrDefault(path, Collections.emptyList()).contains(annotationType));

        }).collect(Collectors.toSet());
    }
}
