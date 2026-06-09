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

package net.croz.nrich.problemdetail.api.service;

import net.croz.nrich.problemdetail.api.model.ValidationError;
import org.springframework.validation.Errors;
import org.springframework.validation.method.ParameterValidationResult;

import jakarta.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;

/**
 * Resolves validation errors into structured {@link ValidationError} entries for the ProblemDetail
 * {@code errors[]} extension. Implementations key on the target class (not the binding object name)
 * so per-DTO message customization is stable across parameter renames.
 */
public interface ValidationErrorResolvingService {

    /**
     * Resolve from a Spring {@link Errors} (BindingResult) - the {@code MethodArgumentNotValidException} path.
     *
     * @param errors Spring binding errors.
     * @param target Target class whose validation failed (for class-stable code resolution).
     * @return resolved validation errors
     */
    List<ValidationError> resolve(Errors errors, Class<?> target);

    /**
     * Resolve from a set of Jakarta Bean Validation violations - the service-layer
     * {@code ConstraintViolationException} path.
     *
     * @param violations constraint violations
     * @return resolved validation errors
     */
    List<ValidationError> resolve(Set<? extends ConstraintViolation<?>> violations);

    /**
     * Resolve from controller method-parameter validation results - the
     * {@code HandlerMethodValidationException} path (constraints directly on
     * {@code @RequestParam} / {@code @PathVariable} parameters and {@code @Valid} bean parameters, on Spring 6.1+).
     *
     * @param parameterValidationResults per-parameter validation results
     * @return resolved validation errors
     */
    List<ValidationError> resolve(List<ParameterValidationResult> parameterValidationResults);

}
