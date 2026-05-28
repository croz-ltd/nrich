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

import lombok.RequiredArgsConstructor;
import net.croz.nrich.problemdetail.api.model.ValidationError;
import net.croz.nrich.problemdetail.api.service.ValidationErrorResolvingService;
import net.croz.nrich.problemdetail.constant.ProblemDetailConstants;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@RequiredArgsConstructor
public class DefaultValidationErrorResolvingService implements ValidationErrorResolvingService {

    private final MessageSource messageSource;

    private final boolean includeRejectedValue;

    @Override
    public List<ValidationError> resolve(Errors errors, Class<?> target) {
        Locale locale = LocaleContextHolder.getLocale();

        return errors.getAllErrors().stream()
            .map(error -> error instanceof FieldError fieldError ? fieldEntry(target, fieldError, locale) : objectEntry(target, error, locale))
            .toList();
    }

    @Override
    public List<ValidationError> resolve(Set<? extends ConstraintViolation<?>> violations) {
        Locale locale = LocaleContextHolder.getLocale();

        return violations.stream()
            .map(violation -> violationEntry(violation, locale))
            .toList();
    }

    @Override
    public List<ValidationError> resolve(List<ParameterValidationResult> parameterValidationResults) {
        Locale locale = LocaleContextHolder.getLocale();

        return parameterValidationResults.stream()
            .flatMap(result -> parameterEntryList(result, locale).stream())
            .toList();
    }

    private ValidationError fieldEntry(Class<?> target, FieldError fieldError, Locale locale) {
        String constraint = fieldError.getCode();
        List<String> codes = new ArrayList<>();

        codes.add(String.format(ProblemDetailConstants.VALIDATION_FQCN_CODE_FORMAT, constraint, target.getName(), fieldError.getField()));
        if (fieldError.getCodes() != null) {
            codes.addAll(Arrays.asList(fieldError.getCodes()));
        }

        String message = messageSource.getMessage(new DefaultMessageSourceResolvable(codes.toArray(String[]::new), fieldError.getArguments(), fieldError.getDefaultMessage()), locale);

        return new ValidationError(fieldError.getField(), rejectedValue(fieldError.getRejectedValue()), constraint, message);
    }

    private ValidationError objectEntry(Class<?> target, ObjectError objectError, Locale locale) {
        String constraint = objectError.getCode();
        List<String> codes = new ArrayList<>();

        codes.add(String.format(ProblemDetailConstants.VALIDATION_CONSTRAINT_FIELD_FORMAT, constraint, target.getName()));
        if (objectError.getCodes() != null) {
            codes.addAll(Arrays.asList(objectError.getCodes()));
        }

        String message = messageSource.getMessage(new DefaultMessageSourceResolvable(codes.toArray(String[]::new), objectError.getArguments(), objectError.getDefaultMessage()), locale);

        return new ValidationError(null, null, constraint, message);
    }

    private ValidationError violationEntry(ConstraintViolation<?> violation, Locale locale) {
        String constraint = violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
        String field = leafNode(violation.getPropertyPath());
        Class<?> target = violation.getRootBeanClass();

        String[] codes = {
            String.format(ProblemDetailConstants.VALIDATION_FQCN_CODE_FORMAT, constraint, target.getName(), field),
            String.format(ProblemDetailConstants.VALIDATION_CONSTRAINT_FIELD_FORMAT, constraint, field),
            constraint
        };

        String message = messageSource.getMessage(new DefaultMessageSourceResolvable(codes, null, violation.getMessage()), locale);

        return new ValidationError(field, rejectedValue(violation.getInvalidValue()), constraint, message);
    }

    private List<ValidationError> parameterEntryList(ParameterValidationResult result, Locale locale) {
        if (result instanceof ParameterErrors parameterErrors) {
            return resolve(parameterErrors, parameterErrors.getMethodParameter().getParameterType());
        }

        String field = result.getMethodParameter().getParameterName();
        Object argument = result.getArgument();

        return result.getResolvableErrors().stream()
            .map(error -> new ValidationError(field, rejectedValue(argument), constraintCode(error), messageSource.getMessage(error, locale)))
            .toList();
    }

    private String constraintCode(MessageSourceResolvable error) {
        String[] codes = error.getCodes();

        return codes == null || codes.length == 0 ? null : codes[codes.length - 1];
    }

    private Object rejectedValue(Object value) {
        return includeRejectedValue ? value : null;
    }

    private String leafNode(Path path) {
        String last = null;
        for (Path.Node node : path) {
            last = node.getName();
        }

        return last;
    }
}
