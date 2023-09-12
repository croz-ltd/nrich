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

package net.croz.nrich.validation.constraint.validator;

import net.croz.nrich.validation.api.constraint.ValidRange;
import net.croz.nrich.validation.constraint.util.ValidationReflectionUtil;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;

public class ValidRangeValidator implements ConstraintValidator<ValidRange, Object> {

    private String fromPropertyName;

    private String toPropertyName;

    private boolean inclusive;

    @Override
    public void initialize(ValidRange constraintAnnotation) {
        fromPropertyName = constraintAnnotation.fromPropertyName();
        toPropertyName = constraintAnnotation.toPropertyName();
        inclusive = constraintAnnotation.inclusive();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Class<?> type = value.getClass();
        Method fromFieldGetter = ValidationReflectionUtil.findGetterMethod(type, fromPropertyName);
        Method toFieldGetter = ValidationReflectionUtil.findGetterMethod(type, toPropertyName);

        Object fromFieldValue = ValidationReflectionUtil.invokeMethod(fromFieldGetter, value);
        Object toFieldValue = ValidationReflectionUtil.invokeMethod(toFieldGetter, value);

        if (fromFieldValue == null || toFieldValue == null) {
            return true;
        }

        if (!(fromFieldValue instanceof Comparable<?> && toFieldValue instanceof Comparable<?>) || !fromFieldValue.getClass().equals(toFieldValue.getClass())) {
            throw new IllegalArgumentException("Both to and from fields have to be instances of comparable and of same type");
        }

        @SuppressWarnings("unchecked")
        int compareResult = ((Comparable<Object>) fromFieldValue).compareTo(toFieldValue);

        return compareResult < 0 || inclusive && compareResult == 0;
    }
}
