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

package net.croz.nrich.validation.constraint.support.disableconstraints;

import net.croz.nrich.validation.api.constraint.DisableConstraints;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DisableConstraintsAnnotationProcessor {

    private static final Pattern GETTER_METHOD_PATTERN = Pattern.compile("^(get|is)([A-Z].*)$");

    private final ConcurrentMap<Class<?>, Map<String, List<Class<? extends Annotation>>>> disableConstraintsHolderMap = new ConcurrentHashMap<>();

    public Map<String, List<Class<? extends Annotation>>> getDisabledConstraintForType(Class<?> type) {
        return disableConstraintsHolderMap.computeIfAbsent(type, this::createDisableConstraintsPathMap);
    }

    private Map<String, List<Class<? extends Annotation>>> createDisableConstraintsPathMap(Class<?> type) {
        Map<String, List<Class<? extends Annotation>>> pathHolderMap = new HashMap<>();

        ReflectionUtils.doWithFields(type, field -> {
            DisableConstraints[] disableConstraints = field.getAnnotationsByType(DisableConstraints.class);

            registerDisableConstraints(pathHolderMap, disableConstraints, PathUtil.getPath(type, field.getName()), false);
        });

        ReflectionUtils.doWithMethods(type, method -> {
            DisableConstraints[] disableConstraints = method.getAnnotationsByType(DisableConstraints.class);
            Matcher matcher = GETTER_METHOD_PATTERN.matcher(method.getName());

            if (matcher.matches()) {
                String propertyName = StringUtils.uncapitalize(matcher.group(2));

                registerDisableConstraints(pathHolderMap, disableConstraints, PathUtil.getPath(type, propertyName), false);
            }
        });

        Class<?> currentType = type;
        while (currentType.getSuperclass() != null) {
            DisableConstraints[] disableConstraints = currentType.getAnnotationsByType(DisableConstraints.class);

            registerDisableConstraints(pathHolderMap, disableConstraints, PathUtil.getPath(type, null), true);
            currentType = currentType.getSuperclass();
        }

        return pathHolderMap;
    }

    private void registerDisableConstraints(Map<String, List<Class<? extends Annotation>>> pathHolderMap, DisableConstraints[] disableConstraints, String path, boolean isTypeAnnotation) {
        Arrays.stream(disableConstraints).forEach(disableConstraint -> {
            if (StringUtils.hasText(disableConstraint.propertyName()) && !isTypeAnnotation) {
                throw new IllegalArgumentException("Property name not allowed on method or property annotation.");
            }

            String fullPath = PathUtil.getPath(path, disableConstraint.propertyName());
            List<Class<? extends Annotation>> currentConstraints = pathHolderMap.getOrDefault(fullPath, new ArrayList<>());

            currentConstraints.addAll(Arrays.asList(disableConstraint.value()));

            pathHolderMap.put(fullPath, currentConstraints);
        });
    }
}
