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

package net.croz.nrich.notification.testutil;

import org.hibernate.validator.internal.engine.path.PathImpl;

import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotNull;
import javax.validation.metadata.ConstraintDescriptor;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public final class ConstraintViolationGeneratingUtil {

    private ConstraintViolationGeneratingUtil() {
    }

    public static ConstraintViolation<?> createConstraintViolationForTarget(Object target) {
        ConstraintViolation<?> constraintViolation = mock(ConstraintViolation.class);

        doReturn(target).when(constraintViolation).getLeafBean();

        return constraintViolation;
    }

    public static ConstraintViolation<?> createConstraintViolationForTargetConversion(Object target) {
        NotNull annotation = mock(NotNull.class);

        doReturn(NotNull.class).when(annotation).annotationType();

        ConstraintDescriptor<?> constraintDescriptor = mock(ConstraintDescriptor.class);

        doReturn(annotation).when(constraintDescriptor).getAnnotation();

        ConstraintViolation<?> constraintViolation = mock(ConstraintViolation.class);

        doReturn(target).when(constraintViolation).getLeafBean();
        doReturn("message").when(constraintViolation).getMessage();
        doReturn("message.template").when(constraintViolation).getMessageTemplate();
        doReturn(PathImpl.createPathFromString("property")).when(constraintViolation).getPropertyPath();
        doReturn("invalid").when(constraintViolation).getInvalidValue();
        doReturn(constraintDescriptor).when(constraintViolation).getConstraintDescriptor();

        return constraintViolation;
    }
}
