package net.croz.nrich.springboot.condition;

import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;
import java.util.Objects;

public class OnPropertyNotEmptyCondition implements Condition {

    private static final String ANNOTATION_NAME = "net.croz.nrich.springboot.condition.ConditionalOnPropertyNotEmpty";

    private static final String PROPERTY_NAME_ANNOTATION_VALUE = "value";

    @Override
    public boolean matches(final ConditionContext context, final AnnotatedTypeMetadata metadata) {
        final Map<String, Object> attributes = metadata.getAnnotationAttributes(ANNOTATION_NAME);
        final Environment environment = context.getEnvironment();
        final String propertyName = (String) Objects.requireNonNull(attributes).get(PROPERTY_NAME_ANNOTATION_VALUE);

        final String stringPropertyValue = environment.getProperty(propertyName);

        if (stringPropertyValue != null) {
            return !stringPropertyValue.isEmpty();
        }

        final String[] stringList = bindWithExceptionIgnored(environment, propertyName, String[].class);

        if (stringList != null) {
            return stringList.length > 0;
        }

        final Map<?, ?>[] mapList = bindWithExceptionIgnored(environment, propertyName, Map[].class);

        return mapList != null && mapList.length > 0;
    }

    private <T> T bindWithExceptionIgnored(final Environment environment, final String propertyName, final Class<T> type) {
        try {
            return Binder.get(environment).bind(propertyName, type).orElse(null);
        }
        catch (final Exception ignored) {
            return null;
        }
    }
}
