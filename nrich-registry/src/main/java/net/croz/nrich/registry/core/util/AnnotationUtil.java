package net.croz.nrich.registry.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public final class AnnotationUtil {

    private AnnotationUtil() {
    }

    public static boolean isAnnotationPresent(final Field field, final String annotationName) {
        try {
            @SuppressWarnings("unchecked")
            final Class<? extends Annotation> annotation = (Class<? extends Annotation>) Class.forName(annotationName);

            return field.getAnnotationsByType(annotation).length > 0;
        }
        catch (final Exception ignored) {
            return false;
        }
    }

    public static boolean isAnnotationPresent(final Class<?> type, final String annotationName) {
        try {
            @SuppressWarnings("unchecked")
            final Class<? extends Annotation> annotation = (Class<? extends Annotation>) Class.forName(annotationName);

            return type.isAnnotationPresent(annotation);
        }
        catch (final Exception ignored) {
            return false;
        }
    }
}
