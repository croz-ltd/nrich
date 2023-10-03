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

import net.croz.nrich.validation.constraint.support.disableconstraints.stub.DisableConstraintsAnnotationProcessorInvalidTestRequest;
import net.croz.nrich.validation.constraint.support.disableconstraints.stub.DisableConstraintsAnnotationProcessorTestRequest;
import org.junit.jupiter.api.Test;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class DisableConstraintsAnnotationProcessorTest {

    private final DisableConstraintsAnnotationProcessor constraintAnnotationProcessor = new DisableConstraintsAnnotationProcessor();

    @Test
    void shouldGetDisabledConstraintsForType() {
        // when
        Map<String, List<Class<? extends Annotation>>> result = constraintAnnotationProcessor.getDisabledConstraintForType(DisableConstraintsAnnotationProcessorTestRequest.class);

        // then
        assertThat(result).containsExactlyInAnyOrderEntriesOf(
            Map.of(
                "net.croz.nrich.validation.constraint.support.disableconstraints.stub.DisableConstraintsAnnotationProcessorTestRequest", List.of(NotNull.class),
                "net.croz.nrich.validation.constraint.support.disableconstraints.stub.DisableConstraintsAnnotationProcessorTestRequest.age", List.of(NotNull.class),
                "net.croz.nrich.validation.constraint.support.disableconstraints.stub.DisableConstraintsAnnotationProcessorTestRequest.employmentDuration", List.of(Min.class),
                "net.croz.nrich.validation.constraint.support.disableconstraints.stub.DisableConstraintsAnnotationProcessorTestRequest.name", List.of(NotBlank.class, Size.class)
            )
        );
    }

    @Test
    void shouldThrowExceptionWhenPropertyNameIsDefinedOnNonTypeAnnotation() {
        // when
        Throwable thrown = catchThrowable(() -> constraintAnnotationProcessor.getDisabledConstraintForType(DisableConstraintsAnnotationProcessorInvalidTestRequest.class));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage("Property name not allowed on method or property annotation.");
    }
}
