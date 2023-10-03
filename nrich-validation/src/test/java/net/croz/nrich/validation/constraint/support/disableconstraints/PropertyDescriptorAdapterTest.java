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

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.metadata.ConstraintDescriptor;
import jakarta.validation.metadata.ContainerElementTypeDescriptor;
import jakarta.validation.metadata.ElementDescriptor;
import jakarta.validation.metadata.GroupConversionDescriptor;
import jakarta.validation.metadata.PropertyDescriptor;
import java.util.List;
import java.util.Set;

import static net.croz.nrich.validation.constraint.support.disableconstraints.testutil.ConstraintDescriptorTestUtil.createConstraintDescriptor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class PropertyDescriptorAdapterTest {

    @Mock
    private PropertyDescriptor target;

    private PropertyDescriptorAdapter propertyDescriptorAdapter;

    @BeforeEach
    void setup() {
        propertyDescriptorAdapter = new PropertyDescriptorAdapter(target, List.of(NotNull.class));
    }

    @Test
    void shouldGetPropertyName() {
        // given
        String propertyName = "propertyName";

        doReturn(propertyName).when(target).getPropertyName();

        // when
        String result = propertyDescriptorAdapter.getPropertyName();

        // then
        assertThat(result).isEqualTo(propertyName);
    }

    @Test
    void shouldReturnWhetherIsCascaded() {
        // given
        doReturn(true).when(target).isCascaded();

        // when
        boolean result = propertyDescriptorAdapter.isCascaded();

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldGetGroupConversions() {
        // given
        Set<GroupConversionDescriptor> groupConversionDescriptors = Set.of(mock(GroupConversionDescriptor.class));

        doReturn(groupConversionDescriptors).when(target).getGroupConversions();

        // when
        Set<GroupConversionDescriptor> result = propertyDescriptorAdapter.getGroupConversions();

        // then
        assertThat(result).isEqualTo(groupConversionDescriptors);
    }

    @Test
    void shouldGetConstrainedContainerElementTypes() {
        // given
        Set<ContainerElementTypeDescriptor> containerElementTypeDescriptors = Set.of(mock(ContainerElementTypeDescriptor.class));

        doReturn(containerElementTypeDescriptors).when(target).getConstrainedContainerElementTypes();

        // when
        Set<ContainerElementTypeDescriptor> result = propertyDescriptorAdapter.getConstrainedContainerElementTypes();

        // then
        assertThat(result).isEqualTo(containerElementTypeDescriptors);
    }

    @Test
    void shouldReturnWhetherHasConstraints() {
        // given
        doReturn(true).when(target).hasConstraints();

        // when
        boolean result = propertyDescriptorAdapter.hasConstraints();

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldGetElementClass() {
        // given
        Class<?> elementClass = Object.class;

        doReturn(elementClass).when(target).getElementClass();

        // when
        Class<?> result = propertyDescriptorAdapter.getElementClass();

        // then
        assertThat(result).isEqualTo(elementClass);
    }

    @Test
    void shouldFilterConstraintDescriptors() {
        // given
        ConstraintDescriptor<?> first = createConstraintDescriptor(NotNull.class);
        ConstraintDescriptor<?> second = createConstraintDescriptor(NotEmpty.class);

        doReturn(Set.of(first, second)).when(target).getConstraintDescriptors();

        // when
        Set<ConstraintDescriptor<?>> result = propertyDescriptorAdapter.getConstraintDescriptors();

        // then
        assertThat(result).containsExactly(second);
    }

    @Test
    void shouldGetConstraintFinderAdapter() {
        // given
        ElementDescriptor.ConstraintFinder constraintFinder = mock(ElementDescriptor.ConstraintFinder.class);

        doReturn(constraintFinder).when(target).findConstraints();

        // when
        ElementDescriptor.ConstraintFinder result = propertyDescriptorAdapter.findConstraints();

        // then
        assertThat(result).isInstanceOf(ConstraintFinderAdapter.class);
    }
}
