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

package net.croz.nrich.problemdetail.testutil;

import net.croz.nrich.problemdetail.service.stub.DefaultValidationErrorResolvingServiceTestRequest;
import net.croz.nrich.problemdetail.service.stub.ParameterValidationTestHandler;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.metadata.ConstraintDescriptor;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

public final class ValidationErrorGeneratingUtil {

    public static final String FIELD_NAME = "id";

    public static final String PARAMETER_NAME = "name";

    public static final String PARAMETER_REJECTED_VALUE = "a";

    private static final String OBJECT_NAME = "defaultValidationErrorResolvingServiceTestRequest";

    private static final String FALLBACK_MESSAGE = "must not be null";

    private ValidationErrorGeneratingUtil() {
    }

    public static BindingResult createBindingResultWithFieldError() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new DefaultValidationErrorResolvingServiceTestRequest(), OBJECT_NAME);
        String[] errorCodeList = new String[] {
            "NotNull." + OBJECT_NAME + "." + FIELD_NAME,
            "NotNull." + FIELD_NAME,
            "NotNull.java.lang.String",
            "NotNull"
        };

        bindingResult.addError(new FieldError(OBJECT_NAME, FIELD_NAME, null, false, errorCodeList, null, FALLBACK_MESSAGE));

        return bindingResult;
    }

    public static BindingResult createBindingResultWithObjectError() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new DefaultValidationErrorResolvingServiceTestRequest(), OBJECT_NAME);

        bindingResult.addError(new ObjectError(OBJECT_NAME, new String[] { "CrossField." + OBJECT_NAME, "CrossField" }, null, FALLBACK_MESSAGE));

        return bindingResult;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Set<ConstraintViolation<?>> createConstraintViolations() {
        NotNull annotation = mock();
        ConstraintViolation violation = mock();
        ConstraintDescriptor constraintDescriptor = mock();

        lenient().doReturn(NotNull.class).when(annotation).annotationType();
        lenient().doReturn(annotation).when(constraintDescriptor).getAnnotation();

        lenient().doReturn(constraintDescriptor).when(violation).getConstraintDescriptor();
        lenient().doReturn(DefaultValidationErrorResolvingServiceTestRequest.class).when(violation).getRootBeanClass();
        lenient().doReturn(PathImpl.createPathFromString("save.argument0." + FIELD_NAME)).when(violation).getPropertyPath();
        lenient().doReturn(null).when(violation).getInvalidValue();
        lenient().doReturn(FALLBACK_MESSAGE).when(violation).getMessage();

        return Set.of(violation);
    }

    public static List<ParameterValidationResult> createParameterValidationResultListWithParameterError() {
        MethodParameter methodParameter = createMethodParameter("handleParameter", String.class);
        String[] errorCodeList = new String[] {
            "Size.handleParameter." + PARAMETER_NAME,
            "Size." + PARAMETER_NAME,
            "Size.java.lang.String",
            "Size"
        };

        MessageSourceResolvable resolvableError = new DefaultMessageSourceResolvable(errorCodeList, null, FALLBACK_MESSAGE);

        return List.of(new ParameterValidationResult(methodParameter, PARAMETER_REJECTED_VALUE, List.of(resolvableError), null, null, null, (error, sourceType) -> null));
    }

    public static List<ParameterValidationResult> createParameterValidationResultListWithBeanError() {
        MethodParameter methodParameter = createMethodParameter("handleBean", DefaultValidationErrorResolvingServiceTestRequest.class);

        return List.of(new ParameterErrors(methodParameter, new DefaultValidationErrorResolvingServiceTestRequest(), createBindingResultWithFieldError(), null, null, null));
    }

    private static MethodParameter createMethodParameter(String methodName, Class<?> parameterType) {
        try {
            MethodParameter methodParameter = new MethodParameter(ParameterValidationTestHandler.class.getDeclaredMethod(methodName, parameterType), 0);

            methodParameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());

            return methodParameter;
        }
        catch (NoSuchMethodException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
