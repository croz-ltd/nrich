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

package net.croz.nrich.problemdetail.service;

import net.croz.nrich.problemdetail.api.model.ValidationError;
import net.croz.nrich.problemdetail.service.stub.DefaultValidationErrorResolvingServiceTestRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.method.ParameterValidationResult;

import jakarta.validation.ConstraintViolation;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import static net.croz.nrich.problemdetail.testutil.ValidationErrorGeneratingUtil.FIELD_NAME;
import static net.croz.nrich.problemdetail.testutil.ValidationErrorGeneratingUtil.PARAMETER_NAME;
import static net.croz.nrich.problemdetail.testutil.ValidationErrorGeneratingUtil.PARAMETER_REJECTED_VALUE;
import static net.croz.nrich.problemdetail.testutil.ValidationErrorGeneratingUtil.createBindingResultWithFieldError;
import static net.croz.nrich.problemdetail.testutil.ValidationErrorGeneratingUtil.createBindingResultWithObjectError;
import static net.croz.nrich.problemdetail.testutil.ValidationErrorGeneratingUtil.createConstraintViolations;
import static net.croz.nrich.problemdetail.testutil.ValidationErrorGeneratingUtil.createParameterValidationResultListWithBeanError;
import static net.croz.nrich.problemdetail.testutil.ValidationErrorGeneratingUtil.createParameterValidationResultListWithParameterError;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class DefaultValidationErrorResolvingServiceTest {

    @Mock
    private MessageSource messageSource;

    private DefaultValidationErrorResolvingService resolvingService;

    @BeforeEach
    void setup() {
        resolvingService = new DefaultValidationErrorResolvingService(messageSource, true);

        doAnswer(invocation -> {
            DefaultMessageSourceResolvable resolvable = invocation.getArgument(0);
            return Objects.requireNonNull(resolvable.getCodes())[0];
        }).when(messageSource).getMessage(any(DefaultMessageSourceResolvable.class), any(Locale.class));
    }

    @Test
    void shouldResolveValidationErrorForFieldError() {
        // given
        BindingResult bindingResult = createBindingResultWithFieldError();

        // when
        List<ValidationError> result = resolvingService.resolve(bindingResult, DefaultValidationErrorResolvingServiceTestRequest.class);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).field()).isEqualTo(FIELD_NAME);
        assertThat(result.get(0).code()).isEqualTo("NotNull");
        assertThat(result.get(0).message()).isEqualTo("NotNull." + DefaultValidationErrorResolvingServiceTestRequest.class.getName() + "." + FIELD_NAME);
    }

    @Test
    void shouldResolveValidationErrorForObjectError() {
        // given
        BindingResult bindingResult = createBindingResultWithObjectError();

        // when
        List<ValidationError> result = resolvingService.resolve(bindingResult, DefaultValidationErrorResolvingServiceTestRequest.class);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).field()).isNull();
        assertThat(result.get(0).code()).isEqualTo("CrossField");
        assertThat(result.get(0).message()).isEqualTo("CrossField." + DefaultValidationErrorResolvingServiceTestRequest.class.getName());
    }

    @Test
    void shouldResolveValidationErrorForConstraintViolation() {
        // given
        Set<ConstraintViolation<?>> violations = createConstraintViolations();

        // when
        List<ValidationError> result = resolvingService.resolve(violations);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).field()).isEqualTo(FIELD_NAME);
        assertThat(result.get(0).code()).isEqualTo("NotNull");
        assertThat(result.get(0).message()).isEqualTo("NotNull." + DefaultValidationErrorResolvingServiceTestRequest.class.getName() + "." + FIELD_NAME);
    }

    @Test
    void shouldResolveValidationErrorForMethodParameterError() {
        // given
        List<ParameterValidationResult> parameterValidationResults = createParameterValidationResultListWithParameterError();

        // when
        List<ValidationError> result = resolvingService.resolve(parameterValidationResults);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).field()).isEqualTo(PARAMETER_NAME);
        assertThat(result.get(0).rejectedValue()).isEqualTo(PARAMETER_REJECTED_VALUE);
        assertThat(result.get(0).code()).isEqualTo("Size");
        assertThat(result.get(0).message()).isEqualTo("Size.handleParameter." + PARAMETER_NAME);
    }

    @Test
    void shouldResolveValidationErrorForMethodParameterBeanError() {
        // given
        List<ParameterValidationResult> parameterValidationResults = createParameterValidationResultListWithBeanError();

        // when
        List<ValidationError> result = resolvingService.resolve(parameterValidationResults);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).field()).isEqualTo(FIELD_NAME);
        assertThat(result.get(0).code()).isEqualTo("NotNull");
        assertThat(result.get(0).message()).isEqualTo("NotNull." + DefaultValidationErrorResolvingServiceTestRequest.class.getName() + "." + FIELD_NAME);
    }

    @Test
    void shouldNotIncludeRejectedValueWhenRejectedValueInclusionIsDisabled() {
        // given
        DefaultValidationErrorResolvingService service = new DefaultValidationErrorResolvingService(messageSource, false);
        List<ParameterValidationResult> parameterValidationResults = createParameterValidationResultListWithParameterError();

        // when
        List<ValidationError> result = service.resolve(parameterValidationResults);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).rejectedValue()).isNull();
        assertThat(result.get(0).field()).isEqualTo(PARAMETER_NAME);
        assertThat(result.get(0).code()).isEqualTo("Size");
    }
}
