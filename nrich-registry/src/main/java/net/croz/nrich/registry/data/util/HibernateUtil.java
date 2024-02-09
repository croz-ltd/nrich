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

package net.croz.nrich.registry.data.util;

import lombok.SneakyThrows;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.BeanUtils;

import jakarta.persistence.Entity;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class HibernateUtil {

    private static final String PROPERTY_PATH_FORMAT = "%s.%s";

    private static final String COLLECTION_ELEMENT_NAME_FORMAT = "%s-%s";

    private HibernateUtil() {
    }

    public static void initialize(Object entity) {
        initializeInternal(entity, null, new ArrayList<>());
    }

    private static void initializeInternal(Object entity, String propertyPath, List<String> alreadyInitializedProperties) {
        if (entity == null || alreadyInitializedProperties.contains(propertyPath)) {
            return;
        }

        Class<?> entityType = resolveEntityType(entity);

        if (!isManagedType(entityType)) {
            return;
        }

        if (!Hibernate.isInitialized(entity)) {
            Hibernate.initialize(entity);
        }

        alreadyInitializedProperties.add(propertyPath);

        Arrays.stream(BeanUtils.getPropertyDescriptors(entityType)).forEach(propertyDescriptor -> {
            String propertyName = propertyDescriptor.getName();
            Object propertyValue = getPropertyValue(entity, propertyDescriptor);

            if (propertyValue instanceof Collection<?> collection) {
                int index = 0;
                for (Object collectionElementValue : collection) {
                    String collectionElementPropertyName = String.format(COLLECTION_ELEMENT_NAME_FORMAT, propertyName, index++);
                    String calculatedPropertyPath = calculatePropertyPath(propertyPath, collectionElementPropertyName);

                    initializeInternal(collectionElementValue, calculatedPropertyPath, alreadyInitializedProperties);
                }
            }
            else {
                String calculatedPropertyPath = calculatePropertyPath(propertyPath, propertyName);

                initializeInternal(propertyValue, calculatedPropertyPath, alreadyInitializedProperties);
            }
        });
    }

    @SneakyThrows
    private static Object getPropertyValue(Object entity, PropertyDescriptor propertyDescriptor) {
        Method method = propertyDescriptor.getReadMethod();

        if (method == null) {
            return null;
        }

        return method.invoke(entity);
    }

    private static Class<?> resolveEntityType(Object entity) {
        Class<?> type = entity.getClass();

        if (entity instanceof HibernateProxy hibernateProxy) {
            type = hibernateProxy.getHibernateLazyInitializer().getPersistentClass();
        }

        return type;
    }

    private static boolean isManagedType(Class<?> entityType) {
        return entityType.getAnnotation(Entity.class) != null;
    }

    private static String calculatePropertyPath(String existingPropertyPath, String propertyName) {
        return existingPropertyPath == null ? propertyName : String.format(PROPERTY_PATH_FORMAT, existingPropertyPath, propertyName);
    }
}
