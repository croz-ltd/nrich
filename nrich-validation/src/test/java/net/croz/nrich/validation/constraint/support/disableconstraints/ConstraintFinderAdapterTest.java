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
import jakarta.validation.metadata.ElementDescriptor;
import jakarta.validation.metadata.Scope;
import java.lang.annotation.ElementType;
import java.util.List;
import java.util.Set;

import static net.croz.nrich.validation.constraint.support.disableconstraints.testutil.ConstraintDescriptorTestUtil.createConstraintDescriptor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConstraintFinderAdapterTest {

    @Mock
    private ElementDescriptor.ConstraintFinder target;

    private ConstraintFinderAdapter constraintFinderAdapter;

    @BeforeEach
    void setup() {
        constraintFinderAdapter = new ConstraintFinderAdapter(target, List.of(NotNull.class));
    }

    @Test
    void shouldSpecifyUnorderedAndMatchingGroupsOnTarget() {
        // given
        Class<?> group = Object.class;

        // when
        ElementDescriptor.ConstraintFinder result = constraintFinderAdapter.unorderedAndMatchingGroups(group);

        // then
        assertThat(result).isInstanceOf(ConstraintFinderAdapter.class);
        verify(target).unorderedAndMatchingGroups(group);
    }

    @Test
    void shouldSpecifyLookingAtOnTarget() {
        // given
        Scope scope = Scope.LOCAL_ELEMENT;

        doReturn(target).when(target).lookingAt(scope);

        // when
        ElementDescriptor.ConstraintFinder result = constraintFinderAdapter.lookingAt(scope);

        // then
        assertThat(result).isInstanceOf(ConstraintFinderAdapter.class);
        verify(target).lookingAt(scope);
    }

    @Test
    void shouldSpecifyDeclaredOnTarget() {
        // given
        ElementType elementType = ElementType.TYPE;

        // when
        ElementDescriptor.ConstraintFinder result = constraintFinderAdapter.declaredOn(elementType);

        // then
        assertThat(result).isInstanceOf(ConstraintFinderAdapter.class);
        verify(target).declaredOn(elementType);
    }

    @Test
    void shouldFilterConstraintDescriptors() {
        // given
        ConstraintDescriptor<?> first = createConstraintDescriptor(NotNull.class);
        ConstraintDescriptor<?> second = createConstraintDescriptor(NotEmpty.class);

        doReturn(Set.of(first, second)).when(target).getConstraintDescriptors();

        // when
        Set<ConstraintDescriptor<?>> result = constraintFinderAdapter.getConstraintDescriptors();

        // then
        assertThat(result).containsExactly(second);
    }

    @Test
    void shouldReturnWhetherTargetHasConstraints() {
        // given
        doReturn(true).when(target).hasConstraints();

        // when
        boolean result = constraintFinderAdapter.hasConstraints();

        // then
        assertThat(result).isTrue();
    }
}
