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

package net.croz.nrich.jackson.serializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.PropertyName;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.introspect.AnnotatedMethod;
import tools.jackson.databind.introspect.BeanPropertyDefinition;
import tools.jackson.databind.ser.BeanPropertyWriter;
import tools.jackson.databind.ser.ValueSerializerModifier;
import tools.jackson.databind.type.SimpleType;
import tools.jackson.databind.util.SimpleBeanPropertyDefinition;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class EntityClassSerializerModifier extends ValueSerializerModifier {

    private static final String ENTITY_ANNOTATION = "jakarta.persistence.Entity";

    private final boolean serializeEntityAnnotatedClasses;

    private final List<String> packageList;

    @SneakyThrows
    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig serializationConfig, BeanDescription.Supplier beanDescription, List<BeanPropertyWriter> beanPropertyList) {

        Class<?> type = beanDescription.getBeanClass();

        if (serializeEntityAnnotatedClasses && isEntity(type) || packageList != null && packageList.contains(type.getPackage().getName())) {
            Method method = type.getMethod("getClass");
            AnnotatedMethod annotatedMethod = new AnnotatedMethod(null, method, null, null);

            BeanPropertyDefinition beanPropertyDefinition = SimpleBeanPropertyDefinition.construct(serializationConfig, annotatedMethod, PropertyName.construct("class"));
            JavaType javaType = SimpleType.constructUnsafe(String.class);
            ValueSerializer<Class<?>> classNameSerializer = new EntityClassNameSerializer();

            BeanPropertyWriter beanPropertyWriter = new BeanPropertyWriter(beanPropertyDefinition, annotatedMethod, null, javaType, classNameSerializer, null, null, true, null, null, JsonInclude.Value.empty());

            beanPropertyList.add(beanPropertyWriter);
        }

        return beanPropertyList;
    }

    private boolean isEntity(Class<?> type) {
        return Arrays.stream(type.getAnnotations())
            .anyMatch(annotation -> ENTITY_ANNOTATION.equals(annotation.annotationType().getName()));
    }
}
