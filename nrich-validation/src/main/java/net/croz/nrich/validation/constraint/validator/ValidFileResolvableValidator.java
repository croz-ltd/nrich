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

package net.croz.nrich.validation.constraint.validator;

import net.croz.nrich.validation.api.constraint.ValidFileResolvable;
import org.springframework.core.env.Environment;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Optional;

public class ValidFileResolvableValidator extends BaseValidFileValidator implements ConstraintValidator<ValidFileResolvable, Object> {

    private final Environment environment;

    public ValidFileResolvableValidator(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void initialize(ValidFileResolvable constraintAnnotation) {
        this.allowedContentTypeList = resolvePropertyValue(constraintAnnotation.allowedContentTypeListPropertyName(), String[].class, new String[0]);
        this.allowedExtensionList = resolvePropertyValue(constraintAnnotation.allowedExtensionListPropertyName(), String[].class, new String[0]);
        this.allowedFileNameRegex = resolvePropertyValue(constraintAnnotation.allowedFileNameRegexPropertyName(), String.class, "");
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return isValid(value);
    }

    private <T> T resolvePropertyValue(String propertyName, Class<T> propertyType, T defaultPropertyValue) {
        if (!propertyName.isEmpty()) {
            return Optional.ofNullable(environment.getProperty(propertyName, propertyType)).orElse(defaultPropertyValue);
        }

        return defaultPropertyValue;
    }
}
