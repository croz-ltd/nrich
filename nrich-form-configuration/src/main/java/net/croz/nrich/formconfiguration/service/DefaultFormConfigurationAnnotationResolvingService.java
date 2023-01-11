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

package net.croz.nrich.formconfiguration.service;

import lombok.SneakyThrows;
import net.croz.nrich.formconfiguration.api.annotation.FormValidationConfiguration;
import net.croz.nrich.formconfiguration.api.service.FormConfigurationAnnotationResolvingService;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DefaultFormConfigurationAnnotationResolvingService implements FormConfigurationAnnotationResolvingService {

    @Override
    public Map<String, Class<?>> resolveFormConfigurations(List<String> packageList) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);

        scanner.addIncludeFilter(new AnnotationTypeFilter(FormValidationConfiguration.class));

        List<String> resolvedPackageList = Optional.ofNullable(packageList).orElse(Collections.emptyList());
        Map<String, Class<?>> result = new HashMap<>();

        resolvedPackageList.forEach(basePackage -> {
            Map<String, Class<?>> packageResult = findAnnotatedClassesInPackage(scanner, basePackage);

            packageResult.forEach(result::putIfAbsent);
        });

        return result;
    }

    private Map<String, Class<?>> findAnnotatedClassesInPackage(ClassPathScanningCandidateComponentProvider scanner, String basePackage) {
        return scanner.findCandidateComponents(basePackage).stream()
            .filter(beanDefinition -> StringUtils.hasText(beanDefinition.getBeanClassName()))
            .map(beanDefinition -> toClass(beanDefinition.getBeanClassName()))
            .collect(Collectors.toMap(type -> type.getAnnotation(FormValidationConfiguration.class).value(), Function.identity()));
    }

    @SneakyThrows
    private Class<?> toClass(String className) {
        return Class.forName(className, true, Thread.currentThread().getContextClassLoader());
    }
}
