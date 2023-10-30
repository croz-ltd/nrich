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

package net.croz.nrich.validation.constraint.support.disableconstraints.testutil;

import jakarta.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public final class ConstraintDescriptorTestUtil {

    private ConstraintDescriptorTestUtil() {
    }

    public static<T extends Annotation> ConstraintDescriptor<T> createConstraintDescriptor(Class<T> constraintAnnotationType) {
        T annotation = mock(constraintAnnotationType);
        doReturn(constraintAnnotationType).when(annotation).annotationType();

        @SuppressWarnings("unchecked")
        ConstraintDescriptor<T> constraintDescriptor = mock(ConstraintDescriptor.class);
        doReturn(annotation).when(constraintDescriptor).getAnnotation();

        return constraintDescriptor;
    }

}
