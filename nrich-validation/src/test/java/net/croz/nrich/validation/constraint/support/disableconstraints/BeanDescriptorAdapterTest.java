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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.metadata.BeanDescriptor;
import jakarta.validation.metadata.ConstraintDescriptor;
import jakarta.validation.metadata.ConstructorDescriptor;
import jakarta.validation.metadata.ElementDescriptor;
import jakarta.validation.metadata.MethodDescriptor;
import jakarta.validation.metadata.MethodType;
import jakarta.validation.metadata.PropertyDescriptor;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BeanDescriptorAdapterTest {

    @Mock
    private BeanDescriptor target;

    private BeanDescriptorAdapter beanDescriptorAdapter;

    @BeforeEach
    void setUp() {
        beanDescriptorAdapter = new BeanDescriptorAdapter(target, Map.of("propertyName", List.of(NotNull.class)));
    }

    @Test
    void shouldReturnWhetherBeanIsConstrained() {
        // given
        doReturn(true).when(target).isBeanConstrained();

        // when
        boolean result = beanDescriptorAdapter.isBeanConstrained();

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldGetConstraintsForProperty() {
        // given
        String propertyName = "propertyName";

        doReturn(mock(PropertyDescriptor.class)).when(target).getConstraintsForProperty(propertyName);

        // when
        PropertyDescriptor result = beanDescriptorAdapter.getConstraintsForProperty(propertyName);

        // then
        assertThat(result).isInstanceOf(PropertyDescriptorAdapter.class);
    }

    @Test
    void shouldGetConstrainedProperties() {
        // given
        when(target.getConstrainedProperties()).thenReturn(Set.of(mock(PropertyDescriptor.class)));

        doReturn(Object.class).when(target).getElementClass();

        // when
        Set<PropertyDescriptor> result = beanDescriptorAdapter.getConstrainedProperties();

        // then
        assertThat(result).isNotEmpty().allMatch(value -> value instanceof PropertyDescriptorAdapter);
    }

    @Test
    void shouldGetConstraintsForMethod() {
        // given
        String methodName = "exampleMethod";
        Class<?>[] parameterTypes = new Class<?>[] { String.class, Integer.class };
        MethodDescriptor methodDescriptor = mock(MethodDescriptor.class);

        doReturn(methodDescriptor).when(target).getConstraintsForMethod(methodName, parameterTypes);

        // when
        MethodDescriptor result = beanDescriptorAdapter.getConstraintsForMethod(methodName, parameterTypes);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void shouldGetConstrainedMethods() {
        // given
        MethodType methodType = MethodType.GETTER;
        MethodType[] methodTypes = new MethodType[] { MethodType.NON_GETTER };
        Set<MethodDescriptor> methodDescriptors = Set.of(mock(MethodDescriptor.class));

        doReturn(methodDescriptors).when(target).getConstrainedMethods(methodType, methodTypes);

        // when
        Set<MethodDescriptor> result = beanDescriptorAdapter.getConstrainedMethods(methodType, methodTypes);

        // then
        assertThat(result).isEqualTo(methodDescriptors);
    }

    @Test
    void shouldGetConstraintsForConstructor() {
        // given
        Class<?>[] parameterTypes = new Class<?>[] { String.class, Integer.class };
        ConstructorDescriptor constructorDescriptor = mock(ConstructorDescriptor.class);

        doReturn(constructorDescriptor).when(target).getConstraintsForConstructor(parameterTypes);

        // when
        ConstructorDescriptor result = beanDescriptorAdapter.getConstraintsForConstructor(parameterTypes);

        // then
        assertThat(result).isEqualTo(constructorDescriptor);
    }

    @Test
    void shouldGetConstrainedConstructors() {
        // given
        Set<ConstructorDescriptor> constructorDescriptors = Set.of(mock(ConstructorDescriptor.class));

        doReturn(constructorDescriptors).when(target).getConstrainedConstructors();

        // when
        Set<ConstructorDescriptor> result = beanDescriptorAdapter.getConstrainedConstructors();

        // then
        assertThat(result).isEqualTo(constructorDescriptors);
    }

    @Test
    void shouldCheckWhetherBeanHasConstraints() {
        // given
        doReturn(true).when(target).hasConstraints();

        // when
        boolean result = beanDescriptorAdapter.hasConstraints();

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldGetElementClass() {
        // given
        Class<?> elementClass = String.class;

        doReturn(elementClass).when(target).getElementClass();

        // when
        Class<?> result = beanDescriptorAdapter.getElementClass();

        // then
        assertThat(result).isEqualTo(elementClass);
    }

    @Test
    void shouldGetConstraintDescriptors() {
        // given
        @SuppressWarnings("unchecked")
        Set<ConstraintDescriptor<?>> constraintDescriptors = Set.of(mock(ConstraintDescriptor.class));

        doReturn(constraintDescriptors).when(target).getConstraintDescriptors();

        // when
        Set<ConstraintDescriptor<?>> result = beanDescriptorAdapter.getConstraintDescriptors();

        // then
        assertThat(result).isEqualTo(constraintDescriptors);
    }

    @Test
    void shouldGetConstraintFinderAdapter() {
        // given
        ElementDescriptor.ConstraintFinder finder = mock(ElementDescriptor.ConstraintFinder.class);

        doReturn(finder).when(target).findConstraints();
        doReturn(Object.class).when(target).getElementClass();

        // when
        ElementDescriptor.ConstraintFinder result = beanDescriptorAdapter.findConstraints();

        // then
        assertThat(result).isInstanceOf(ConstraintFinderAdapter.class);
    }
}
