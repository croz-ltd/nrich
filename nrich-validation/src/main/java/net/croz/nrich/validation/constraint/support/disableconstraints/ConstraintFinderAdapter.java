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

import jakarta.validation.metadata.ConstraintDescriptor;
import jakarta.validation.metadata.ElementDescriptor;
import jakarta.validation.metadata.Scope;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ConstraintFinderAdapter implements ElementDescriptor.ConstraintFinder {

    private final ElementDescriptor.ConstraintFinder target;

    private final List<Class<? extends Annotation>> disabledConstraintTypes;

    @Override
    public Set<ConstraintDescriptor<?>> getConstraintDescriptors() {
        Set<ConstraintDescriptor<?>> constraintDescriptors = target.getConstraintDescriptors();

        return constraintDescriptors.stream().filter(constraintDescriptor -> !disabledConstraintTypes.contains(constraintDescriptor.getAnnotation().annotationType()))
            .collect(Collectors.toSet());
    }

    @Override
    public ElementDescriptor.ConstraintFinder unorderedAndMatchingGroups(Class<?>... groups) {
        target.unorderedAndMatchingGroups(groups);

        return this;
    }

    @Override
    public ElementDescriptor.ConstraintFinder lookingAt(Scope scope) {
        target.lookingAt(scope);

        return this;
    }

    @Override
    public ElementDescriptor.ConstraintFinder declaredOn(ElementType... types) {
        target.declaredOn(types);

        return this;
    }

    @Override
    public boolean hasConstraints() {
        return target.hasConstraints();
    }
}
