package net.croz.nrich.registry.configuration.util;

import net.croz.nrich.registry.configuration.model.JavascriptType;

import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class JavaToJavascriptTypeConversionUtil {

    private static final Map<Class<?>, JavascriptType> CLASS_JAVASCRIPT_TYPE_MAP = new HashMap<>();

    static {
        CLASS_JAVASCRIPT_TYPE_MAP.put(Boolean.class, JavascriptType.BOOLEAN);
        CLASS_JAVASCRIPT_TYPE_MAP.put(String.class, JavascriptType.STRING);
        CLASS_JAVASCRIPT_TYPE_MAP.put(Character.class, JavascriptType.STRING);
        CLASS_JAVASCRIPT_TYPE_MAP.put(Calendar.class, JavascriptType.DATE);
        CLASS_JAVASCRIPT_TYPE_MAP.put(Date.class, JavascriptType.DATE);
        CLASS_JAVASCRIPT_TYPE_MAP.put(Temporal.class, JavascriptType.DATE);
        CLASS_JAVASCRIPT_TYPE_MAP.put(Number.class, JavascriptType.NUMBER);
    }

    private JavaToJavascriptTypeConversionUtil() {
    }

    public static JavascriptType fromJavaType(final Class<?> type) {
        return CLASS_JAVASCRIPT_TYPE_MAP.entrySet().stream()
                .filter(entry -> entry.getKey().isAssignableFrom(type))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(JavascriptType.OBJECT);
    }
}
