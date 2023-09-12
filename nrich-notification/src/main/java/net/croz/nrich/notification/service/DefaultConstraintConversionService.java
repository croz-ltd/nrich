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

package net.croz.nrich.notification.service;

import net.croz.nrich.notification.api.service.ConstraintConversionService;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.CustomValidatorBean;

import jakarta.validation.ConstraintViolation;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultConstraintConversionService implements ConstraintConversionService {

    private final ValidatorConverter validatorConverter = new ValidatorConverter();

    @Override
    public Object resolveTarget(Set<ConstraintViolation<?>> constraintViolationList) {
        return constraintViolationList.stream()
            .map(ConstraintViolation::getLeafBean)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }

    @Override
    public Errors convertConstraintViolationsToErrors(Set<ConstraintViolation<?>> constraintViolationList, Object target, String targetName) {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(target, targetName);

        Set<ConstraintViolation<Object>> convertedConstraintViolationList = constraintViolationList.stream().map(this::asObjectConstraintViolation).collect(Collectors.toSet());

        validatorConverter.processConstraintViolations(convertedConstraintViolationList, errors);

        return errors;
    }

    @SuppressWarnings("unchecked")
    private ConstraintViolation<Object> asObjectConstraintViolation(ConstraintViolation<?> constraintViolation) {
        return (ConstraintViolation<Object>) constraintViolation;
    }

    private static class ValidatorConverter extends CustomValidatorBean {

        @Override
        public void processConstraintViolations(Set<ConstraintViolation<Object>> violations, Errors errors) {
            super.processConstraintViolations(violations, errors);
        }
    }
}
