package net.croz.nrich.search.util;

import lombok.SneakyThrows;
import net.croz.nrich.search.api.annotation.Projection;
import net.croz.nrich.search.api.model.SearchProjection;
import org.springframework.beans.factory.annotation.Value;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class ProjectionListResolverUtil {

    private ProjectionListResolverUtil() {
    }

    public static <R> List<SearchProjection<R>> resolveSearchProjectionList(final Class<?> projectionType) {
        final Predicate<Field> shouldIncludeField = field -> !(field.isSynthetic() || Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers()));

        return Arrays.stream(projectionType.getDeclaredFields())
                .filter(shouldIncludeField)
                .map(ProjectionListResolverUtil::<R>convertToProjection)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private static  <R> SearchProjection<R> convertToProjection(final Field field) {
        final String alias = field.getName();

        final Annotation[] annotationList = field.getAnnotations();

        String path = alias;
        Predicate<R> condition = (request) -> true;
        if (annotationList != null) {
            final Projection projectionAnnotation = findProjectionAnnotation(annotationList);

            if (projectionAnnotation != null) {
                path = projectionAnnotation.path();

                if (!Projection.DEFAULT.class.equals(projectionAnnotation.condition())) {
                    @SuppressWarnings("unchecked")
                    final Predicate<R> predicate = (Predicate<R>) projectionAnnotation.condition().getDeclaredConstructor().newInstance();
                    condition = predicate;
                }
            }
            else {
                final Value valueAnnotation = findValueAnnotation(annotationList);

                if (valueAnnotation != null) {
                    path = valueAnnotation.value();
                }
            }
        }

        return new SearchProjection<>(path, alias, condition);
    }

    private static Value findValueAnnotation(final Annotation[] annotationList) {
        return (Value) Arrays.stream(annotationList)
                .filter(annotation -> Value.class.isAssignableFrom(annotation.annotationType()))
                .findFirst().orElse(null);
    }

    private static Projection findProjectionAnnotation(final Annotation[] annotationList) {
        return (Projection) Arrays.stream(annotationList)
                .filter(annotation -> Projection.class.isAssignableFrom(annotation.annotationType()))
                .findFirst().orElse(null);
    }
}
