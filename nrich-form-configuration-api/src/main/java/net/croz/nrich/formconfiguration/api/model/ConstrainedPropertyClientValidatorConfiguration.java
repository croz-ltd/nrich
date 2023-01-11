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

package net.croz.nrich.formconfiguration.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * Client validation configuration for single constrained property. A class property with multiple constraints will be resolved to a list of ConstrainedPropertyClientValidatorConfiguration instances.
 */
@RequiredArgsConstructor
@Getter
public class ConstrainedPropertyClientValidatorConfiguration {

    /**
     * Constraint name (i.e. NotNull).
     */
    private final String name;

    /**
     * Constraint arguments as a map.
     */
    private final Map<String, Object> argumentMap;

    /**
     * Error message that should be shown if validation fails.
     */
    private final String errorMessage;

}
