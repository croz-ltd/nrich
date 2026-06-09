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

package net.croz.nrich.problemdetail.contributor;

import net.croz.nrich.problemdetail.api.model.ValidationError;
import net.croz.nrich.problemdetail.api.service.ValidationErrorResolvingService;
import net.croz.nrich.problemdetail.constant.ProblemDetailConstants;
import net.croz.nrich.problemdetail.contributor.stub.ValidationErrorsProblemDetailContributorTestRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static net.croz.nrich.problemdetail.testutil.ProblemDetailGeneratingUtil.createProblemDetail;
import static net.croz.nrich.problemdetail.testutil.ProblemDetailGeneratingUtil.createProblemDetailContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ValidationErrorsProblemDetailContributorTest {

    @Mock
    private ValidationErrorResolvingService validationErrorResolvingService;

    @InjectMocks
    private ValidationErrorsProblemDetailContributor contributor;

    @Test
    void shouldSetErrorsForMethodArgumentNotValidException() {
        // given
        BindingResult bindingResult = mock();
        MethodParameter methodParameter = mock();
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);
        List<ValidationError> resolved = List.of(new ValidationError("id", null, "NotNull", "must not be null"));

        doReturn(ValidationErrorsProblemDetailContributorTestRequest.class).when(methodParameter).getParameterType();
        doReturn(resolved).when(validationErrorResolvingService).resolve(bindingResult, ValidationErrorsProblemDetailContributorTestRequest.class);

        ProblemDetail problemDetail = createProblemDetail(HttpStatus.BAD_REQUEST);

        // when
        contributor.contribute(problemDetail, createProblemDetailContext(exception, HttpStatus.BAD_REQUEST));

        // then
        assertThat(problemDetail.getProperties()).containsEntry(ProblemDetailConstants.ERRORS_PROPERTY, resolved);
    }

    @Test
    void shouldSetErrorsForConstraintViolationException() {
        // given
        ConstraintViolationException exception = new ConstraintViolationException(Collections.emptySet());
        List<ValidationError> resolved = List.of(new ValidationError("id", null, "NotNull", "must not be null"));
        ProblemDetail problemDetail = createProblemDetail(HttpStatus.BAD_REQUEST);

        doReturn(resolved).when(validationErrorResolvingService).resolve(any(Set.class));

        // when
        contributor.contribute(problemDetail, createProblemDetailContext(exception, HttpStatus.BAD_REQUEST));

        // then
        assertThat(problemDetail.getProperties()).containsEntry(ProblemDetailConstants.ERRORS_PROPERTY, resolved);
    }

    @Test
    void shouldNotAttachErrorsForOtherExceptions() {
        // given
        ProblemDetail problemDetail = createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR);

        // when
        contributor.contribute(problemDetail, createProblemDetailContext(new RuntimeException("exception")));

        // then
        assertThat(problemDetail.getProperties()).isNullOrEmpty();
        verifyNoInteractions(validationErrorResolvingService);
    }

    @Test
    void shouldExposeConfiguredOrder() {
        // expect
        assertThat(contributor.getOrder()).isEqualTo(ProblemDetailConstants.VALIDATION_ERRORS_CONTRIBUTOR_ORDER);
    }

}
