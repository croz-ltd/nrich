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

import net.croz.nrich.validation.api.constraint.ValidSearchProperties;
import net.croz.nrich.validation.constraint.util.ValidationReflectionUtil;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ValidSearchPropertiesValidator implements ConstraintValidator<ValidSearchProperties, Object> {

    private Map<String, String[]> propertyGroupMap;

    @Override
    public void initialize(ValidSearchProperties constraintAnnotation) {
        ValidSearchProperties.PropertyGroup[] propertyGroupList = constraintAnnotation.propertyGroup();

        propertyGroupMap = IntStream.range(0, propertyGroupList.length)
            .boxed()
            .collect(Collectors.toConcurrentMap(Object::toString, value -> propertyGroupList[value].value()));
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Class<?> type = value.getClass();

        return propertyGroupMap.entrySet().stream().anyMatch(fieldGroup -> {
            List<Method> methodList = Arrays.stream(fieldGroup.getValue())
                .map(fieldName -> ValidationReflectionUtil.findGetterMethod(type, fieldName))
                .collect(Collectors.toList());

            return methodList.stream().allMatch(method -> ValidationReflectionUtil.invokeMethod(method, value) != null);
        });
    }
}
