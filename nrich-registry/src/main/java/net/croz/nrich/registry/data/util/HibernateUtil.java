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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public final class HibernateUtil {

    private HibernateUtil() {
    }

    public static void initialize(Object entity) {
        initializeInternal(entity, Collections.newSetFromMap(new IdentityHashMap<>()));
    }

    private static void initializeInternal(Object entity, Set<Object> visited) {
        if (entity == null || !visited.add(entity)) {
            return;
        }

        Class<?> entityType = resolveEntityType(entity);

        if (!isManagedType(entityType)) {
            return;
        }

        if (!Hibernate.isInitialized(entity)) {
            Hibernate.initialize(entity);
        }

        Arrays.stream(BeanUtils.getPropertyDescriptors(entityType)).forEach(propertyDescriptor -> {
            Object propertyValue = getPropertyValue(entity, propertyDescriptor);

            if (propertyValue instanceof Collection<?> collection) {
                collection.forEach(element -> initializeInternal(element, visited));
            }
            else {
                initializeInternal(propertyValue, visited);
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
}
