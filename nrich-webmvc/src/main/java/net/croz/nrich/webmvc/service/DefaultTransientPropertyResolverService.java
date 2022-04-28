/*
 *  Copyright 2020-2022 CROZ d.o.o, the original author or authors.
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
import java.util.stream.Collectors;

public class DefaultTransientPropertyResolverService implements TransientPropertyResolverService {

    @Cacheable("nrich.transientPropertyResolver.cache")
    @Override
    public List<String> resolveTransientPropertyList(Class<?> type) {
        List<String> transientPropertyList = new ArrayList<>();
        Class<?> currentType = type;

        while (currentType != Object.class) {
            List<String> currentTransientPropertyList = Arrays.stream(currentType.getDeclaredFields())
                .filter(field -> Modifier.isTransient(field.getModifiers()) && !field.isSynthetic())
                .map(Field::getName)
                .collect(Collectors.toList());

            transientPropertyList.addAll(currentTransientPropertyList);

            currentType = currentType.getSuperclass();
        }

        return transientPropertyList;
    }
}
