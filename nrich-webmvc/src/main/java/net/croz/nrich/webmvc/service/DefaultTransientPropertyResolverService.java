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

package net.croz.nrich.webmvc.service;

import org.springframework.cache.annotation.Cacheable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultTransientPropertyResolverService implements TransientPropertyResolverService {

    @Cacheable("nrich.transientPropertyResolver.cache")
    @Override
    public List<String> resolveTransientPropertyList(Class<?> type) {
        List<String> transientPropertyList = new ArrayList<>();
        Class<?> currentType = type;

        while (currentType != null && currentType != Object.class) {
            List<String> currentTransientPropertyList = Arrays.stream(currentType.getDeclaredFields())
                .filter(this::includeField)
                .map(Field::getName)
                .toList();

            transientPropertyList.addAll(currentTransientPropertyList);

            currentType = currentType.getSuperclass();
        }

        return transientPropertyList;
    }

    private boolean includeField(Field field) {
        if (field.isSynthetic()) {
            return false;
        }

        return Modifier.isTransient(field.getModifiers());
    }
}
