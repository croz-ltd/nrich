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

package net.croz.nrich.formconfiguration.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Hold configuration for specific form id. Form id is registered through <pre>{@code Map<String, Class<?>> formIdConstraintHolderMap}</pre> map and maps received
 * form id from client to a class that holds constrained properties.
 */
@RequiredArgsConstructor
@Getter
public class FormConfiguration {

    /**
     * Registered form id for this form configuration.
     */
    private final String formId;

    /**
     * List of {@link ConstrainedPropertyConfiguration} instances holding property configuration for each property defined in the class that form id was mapped to.
     */
    private final List<ConstrainedPropertyConfiguration> constrainedPropertyConfigurationList;

}
