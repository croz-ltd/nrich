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

import lombok.RequiredArgsConstructor;

import jakarta.validation.metadata.BeanDescriptor;
import jakarta.validation.metadata.ConstraintDescriptor;
import jakarta.validation.metadata.ConstructorDescriptor;
import jakarta.validation.metadata.MethodDescriptor;
import jakarta.validation.metadata.MethodType;
import jakarta.validation.metadata.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BeanDescriptorAdapter implements BeanDescriptor {

    private final BeanDescriptor target;

    private final Map<String, List<Class<? extends Annotation>>> disabledConstraintsPathMap;

    @Override
    public PropertyDescriptor getConstraintsForProperty(String propertyName) {
        String path = PathUtil.getPath(target.getElementClass(), propertyName);

        return new PropertyDescriptorAdapter(target.getConstraintsForProperty(propertyName), disabledConstraintsPathMap.getOrDefault(path, Collections.emptyList()));
    }

    @Override
    public Set<PropertyDescriptor> getConstrainedProperties() {
        return target.getConstrainedProperties().stream()
            .map(propertyDescriptor -> getConstraintsForProperty(propertyDescriptor.getPropertyName()))
            .collect(Collectors.toSet());
    }

    @Override
    public ConstraintFinder findConstraints() {
        ConstraintFinder finder = target.findConstraints();
        String path = PathUtil.getPath(target.getElementClass(), null);

        return new ConstraintFinderAdapter(finder, disabledConstraintsPathMap.getOrDefault(path, Collections.emptyList()));
    }

    @Override
    public boolean isBeanConstrained() {
        return target.isBeanConstrained();
    }

    @Override
    public MethodDescriptor getConstraintsForMethod(String methodName, Class<?>... parameterTypes) {
        return target.getConstraintsForMethod(methodName, parameterTypes);
    }

    @Override
    public Set<MethodDescriptor> getConstrainedMethods(MethodType methodType, MethodType... methodTypes) {
        return target.getConstrainedMethods(methodType, methodTypes);
    }

    @Override
    public ConstructorDescriptor getConstraintsForConstructor(Class<?>... parameterTypes) {
        return target.getConstraintsForConstructor(parameterTypes);
    }

    @Override
    public Set<ConstructorDescriptor> getConstrainedConstructors() {
        return target.getConstrainedConstructors();
    }

    @Override
    public boolean hasConstraints() {
        return target.hasConstraints();
    }

    @Override
    public Class<?> getElementClass() {
        return target.getElementClass();
    }

    @Override
    public Set<ConstraintDescriptor<?>> getConstraintDescriptors() {
        return target.getConstraintDescriptors();
    }
}
