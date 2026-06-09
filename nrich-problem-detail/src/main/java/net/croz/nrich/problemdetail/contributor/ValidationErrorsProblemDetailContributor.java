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

import lombok.RequiredArgsConstructor;
import net.croz.nrich.problemdetail.api.contributor.ProblemDetailContributor;
import net.croz.nrich.problemdetail.api.contributor.ProblemDetailContributorContext;
import net.croz.nrich.problemdetail.api.service.ValidationErrorResolvingService;
import net.croz.nrich.problemdetail.constant.ProblemDetailConstants;
import org.springframework.core.Ordered;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import jakarta.validation.ConstraintViolationException;

@RequiredArgsConstructor
public class ValidationErrorsProblemDetailContributor implements ProblemDetailContributor, Ordered {

    private final ValidationErrorResolvingService validationErrorResolvingService;

    @Override
    public void contribute(ProblemDetail problemDetail, ProblemDetailContributorContext context) {
        Exception exception = context.exception();

        if (exception instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
            Class<?> target = methodArgumentNotValidException.getParameter().getParameterType();

            problemDetail.setProperty(ProblemDetailConstants.ERRORS_PROPERTY, validationErrorResolvingService.resolve(methodArgumentNotValidException.getBindingResult(), target));
        }
        else if (exception instanceof ConstraintViolationException constraintViolationException) {
            problemDetail.setProperty(ProblemDetailConstants.ERRORS_PROPERTY, validationErrorResolvingService.resolve(constraintViolationException.getConstraintViolations()));
        }
        else if (exception instanceof HandlerMethodValidationException handlerMethodValidationException) {
            problemDetail.setProperty(ProblemDetailConstants.ERRORS_PROPERTY, validationErrorResolvingService.resolve(handlerMethodValidationException.getParameterValidationResults()));
        }
    }

    @Override
    public int getOrder() {
        return ProblemDetailConstants.VALIDATION_ERRORS_CONTRIBUTOR_ORDER;
    }

}
