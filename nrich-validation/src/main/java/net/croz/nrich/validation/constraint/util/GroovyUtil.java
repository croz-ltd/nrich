package net.croz.nrich.validation.constraint.util;

import org.springframework.util.ClassUtils;

import java.util.regex.Pattern;

public final class GroovyUtil {

    public GroovyUtil() {
    }

    private static final Pattern GROOVY_CLOSURE_PATTERN = Pattern.compile(".*\\$_.*closure.*");

    public static boolean isGroovyPresent() {
        return ClassUtils.isPresent("groovy.lang.MetaClass", null);
    }

    public static boolean isGroovyClosure(Class<?> type) {
        return GROOVY_CLOSURE_PATTERN.matcher(type.getName()).matches();
    }
}
