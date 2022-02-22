package net.croz.nrich.jackson.serializer;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class EntityClassSerializerModifier extends BeanSerializerModifier {

    private static final String ENTITY_ANNOTATION = "javax.persistence.Entity";

    private final boolean serializeEntityAnnotatedClasses;

    private final List<String> packageList;

    @SneakyThrows
    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig serializationConfig, BeanDescription beanDescription, List<BeanPropertyWriter> beanPropertyList) {

        Class<?> type = beanDescription.getType().getRawClass();

        if (serializeEntityAnnotatedClasses && isEntity(type) || packageList != null && packageList.contains(type.getPackage().getName())) {
            Method method = type.getMethod("getClass");
            AnnotatedMethod annotatedMethod = new AnnotatedMethod(null, method, null, null);

            BeanPropertyDefinition beanPropertyDefinition = SimpleBeanPropertyDefinition.construct(serializationConfig, annotatedMethod, PropertyName.construct("class"));
            JavaType javaType = SimpleType.constructUnsafe(String.class);
            JsonSerializer<Class<?>> myJsonSerializer = new EntityClassNameSerializer();

            BeanPropertyWriter beanPropertyWriter = new BeanPropertyWriter(beanPropertyDefinition, annotatedMethod, null, javaType, myJsonSerializer, null, null, true, null, null);

            beanPropertyList.add(beanPropertyWriter);
        }

        return beanPropertyList;
    }

    private boolean isEntity(Class<?> type) {
        return Arrays.stream(type.getAnnotations())
                .anyMatch(annotation -> ENTITY_ANNOTATION.equals(annotation.annotationType().getName()));
    }
}
