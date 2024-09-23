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

package net.croz.nrich.validation.constraint.util;

import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public final class ValidationReflectionUtil {

    private static final String[] METHOD_PATTERN_LIST = { "get%s", "is%s" };

    private ValidationReflectionUtil() {
    }

    public static Method findGetterMethod(Class<?> type, String fieldName) {
        return getterMethodNames(fieldName)
            .map(methodName -> ReflectionUtils.findMethod(type, methodName))
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }

    public static Object invokeMethod(Method method, Object target) {
        return ReflectionUtils.invokeMethod(method, target);
    }

    private static Stream<String> getterMethodNames(String fieldName) {
        String capitalizedFieldName = StringUtils.capitalize(fieldName);

        return Stream.concat(Arrays.stream(METHOD_PATTERN_LIST).map(value -> String.format(value, capitalizedFieldName)), Stream.of(fieldName));
    }
}
