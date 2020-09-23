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

    public static Method findGetterMethod(final Class<?> type, final String fieldName) {
        final String capitalizedFieldName = StringUtils.capitalize(fieldName);

        return Arrays.stream(METHOD_PATTERN_LIST)
                .map(value -> String.format(value, capitalizedFieldName))
                .map(methodName -> ReflectionUtils.findMethod(type, methodName))
                .map(Optional::ofNullable)
                .findFirst()
                .flatMap(Function.identity())
                .orElse(null);
    }

    public static Object invokeMethod(final Method method, final Object target) {
        return ReflectionUtils.invokeMethod(method, target);
    }
}
