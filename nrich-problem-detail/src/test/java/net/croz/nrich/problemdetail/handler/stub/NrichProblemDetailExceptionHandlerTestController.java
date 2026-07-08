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

package net.croz.nrich.problemdetail.handler.stub;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.mock;

@RequiredArgsConstructor
@RequestMapping("problem-detail-test-controller")
@RestController
class NrichProblemDetailExceptionHandlerTestController {

    private final NrichProblemDetailExceptionHandlerTestService nrichProblemDetailExceptionHandlerTestService;

    @PostMapping("generic-exception")
    void genericException() {
        throw new RuntimeException("exception");
    }

    @PostMapping("exception-with-message-code")
    void exceptionWithMessageCode() {
        throw new NrichProblemDetailExceptionHandlerTestException();
    }

    @PostMapping("exception-without-message-code")
    void exceptionWithoutMessageCode() {
        throw new NrichProblemDetailExceptionHandlerTestExceptionWithoutMessageCode();
    }

    @PostMapping("response-status-exception")
    void responseStatusException() {
        throw new NrichProblemDetailExceptionHandlerTestResponseStatusException();
    }

    @PostMapping("status-override-exception")
    void statusOverrideException() {
        throw new NrichProblemDetailExceptionHandlerTestStatusOverrideException();
    }

    @PostMapping("invalid-status-override-exception")
    void invalidStatusOverrideException() {
        throw new NrichProblemDetailExceptionHandlerTestInvalidStatusOverrideException();
    }

    @PostMapping("constraint-violation")
    String constraintViolation() {
        return nrichProblemDetailExceptionHandlerTestService.validationFailedResolving(new NrichProblemDetailExceptionHandlerTestRequest());
    }

    @PostMapping("method-argument-not-valid")
    String methodArgumentNotValid(@Valid @RequestBody NrichProblemDetailExceptionHandlerTestRequest request) {
        return request.getName();
    }

    @PostMapping("bind-validation-failed-resolving")
    String bindValidationFailedResolving(@Valid NrichProblemDetailExceptionHandlerTestRequest request) {
        return request.getName();
    }

    @PostMapping("method-parameter-validation")
    String methodParameterValidation(@RequestParam @Size(min = 2) String name) {
        return name;
    }

    @PostMapping("unwrap-generic-exception")
    void unwrapGenericException() throws ExecutionException {
        throw new ExecutionException(new NrichProblemDetailExceptionHandlerTestException());
    }

    @PostMapping("unwrap-constraint-violation")
    String unwrapConstraintViolation() throws ExecutionException {
        try {
            return nrichProblemDetailExceptionHandlerTestService.validationFailedResolving(new NrichProblemDetailExceptionHandlerTestRequest());
        }
        catch (Exception exception) {
            throw new ExecutionException(exception);
        }
    }

    @PostMapping("unwrap-method-argument-not-valid")
    void unwrapMethodArgumentNotValid() throws ExecutionException, NoSuchMethodException {
        MethodParameter methodParameter = new MethodParameter(getClass().getDeclaredMethod("methodArgumentNotValid", NrichProblemDetailExceptionHandlerTestRequest.class), 0);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, mock(BindingResult.class));

        throw new ExecutionException(exception);
    }

    @PostMapping("not-unwrapped-wrapper")
    void notUnwrappedWrapper() {
        throw new IllegalStateException("wrapper", new RuntimeException("cause"));
    }

}
