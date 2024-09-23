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

package net.croz.nrich.formconfiguration.service;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.formconfiguration.api.model.ConstrainedProperty;
import net.croz.nrich.formconfiguration.api.model.ConstrainedPropertyClientValidatorConfiguration;
import net.croz.nrich.formconfiguration.api.service.ConstrainedPropertyValidatorConverterService;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;

import java.util.List;

@RequiredArgsConstructor
@Order
public class DefaultConstrainedPropertyValidatorConverterService implements ConstrainedPropertyValidatorConverterService {

    private final FieldErrorMessageResolverService fieldErrorMessageResolverService;

    @Override
    public List<ConstrainedPropertyClientValidatorConfiguration> convert(ConstrainedProperty constrainedProperty) {
        String message = fieldErrorMessageResolverService.resolveErrorMessage(constrainedProperty, LocaleContextHolder.getLocale());
        ConstrainedPropertyClientValidatorConfiguration validator = new ConstrainedPropertyClientValidatorConfiguration(
            constrainedProperty.getConstraintName(), constrainedProperty.getConstraintArgumentMap(), message
        );

        return List.of(validator);
    }

    @Override
    public boolean supports(ConstrainedProperty constrainedProperty) {
        return true;
    }
}
