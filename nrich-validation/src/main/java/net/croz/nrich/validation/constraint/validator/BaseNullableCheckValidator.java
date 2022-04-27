/*
 *  Copyright 2020-2022 CROZ d.o.o, the original author or authors.
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

import lombok.RequiredArgsConstructor;
import net.croz.nrich.validation.constraint.util.ValidationReflectionUtil;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.lang.reflect.Method;
import java.util.function.Predicate;

@RequiredArgsConstructor
abstract class BaseNullableCheckValidator {

    private final AutowireCapableBeanFactory beanFactory;

    protected abstract boolean isPropertyValueValid(Object propertyValue);

    protected boolean isValid(Object value, Class<? extends Predicate<?>> conditionClass, String propertyName) {
        if (value == null) {
            return true;
        }

        @SuppressWarnings("unchecked")
        Predicate<Object> condition = (Predicate<Object>) beanFactory.autowire(conditionClass, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false);

        boolean conditionEvaluationResult = condition.test(value);

        if (!conditionEvaluationResult) {
            return true;
        }

        Object propertyValue = resolvePropertyValue(value, propertyName);

        return isPropertyValueValid(propertyValue);
    }

    private Object resolvePropertyValue(Object parent, String propertyName) {
        Method propertyGetterMethod = ValidationReflectionUtil.findGetterMethod(parent.getClass(), propertyName);

        if (propertyGetterMethod == null) {
            throw new IllegalArgumentException(String.format("No getter method found for property %s when invoking %s validator", propertyName, this.getClass().getSimpleName()));
        }

        return ValidationReflectionUtil.invokeMethod(propertyGetterMethod, parent);
    }
}
