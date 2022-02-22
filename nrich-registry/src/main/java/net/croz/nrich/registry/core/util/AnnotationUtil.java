package net.croz.nrich.registry.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public final class AnnotationUtil {

    private AnnotationUtil() {
    }

    public static boolean isAnnotationPresent(Field field, String annotationName) {
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Annotation> annotation = (Class<? extends Annotation>) Class.forName(annotationName);

            return field.getAnnotationsByType(annotation).length > 0;
        }
        catch (Exception ignored) {
            return false;
        }
    }

    public static boolean isAnnotationPresent(Class<?> type, String annotationName) {
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Annotation> annotation = (Class<? extends Annotation>) Class.forName(annotationName);

            return type.isAnnotationPresent(annotation);
        }
        catch (Exception ignored) {
            return false;
        }
    }
}
