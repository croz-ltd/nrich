package net.croz.nrich.validation.constraint.util;

import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

public final class ValidationReflectionUtil {

    private static final String[] METHOD_PATTERN_LIST = { "get%s", "is%s" };

    private ValidationReflectionUtil() {
    }

    public static Method findGetterMethod(Class<?> type, String fieldName) {
        String capitalizedFieldName = StringUtils.capitalize(fieldName);

        return Arrays.stream(METHOD_PATTERN_LIST)
            .map(value -> String.format(value, capitalizedFieldName))
            .map(methodName -> ReflectionUtils.findMethod(type, methodName))
            .map(Optional::ofNullable)
            .findFirst()
            .flatMap(Function.identity())
            .orElse(null);
    }

    public static Object invokeMethod(Method method, Object target) {
        return ReflectionUtils.invokeMethod(method, target);
    }
}
