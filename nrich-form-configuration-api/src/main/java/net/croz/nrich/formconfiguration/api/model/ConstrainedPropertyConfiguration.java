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

import java.util.List;

/**
 * Contains all {@link ConstrainedPropertyClientValidatorConfiguration} instances for a single property with path.
 *
 * @param path           Path to the property relative to a parent class that is mapped to form id.
 * @param propertyType   Type of constrained property.
 * @param javascriptType Javascript type of constrained property.
 * @param validatorList  List of {@link ConstrainedPropertyClientValidatorConfiguration} instances that hold client side validation configuration.
 */
public record ConstrainedPropertyConfiguration(String path, Class<?> propertyType, String javascriptType, List<ConstrainedPropertyClientValidatorConfiguration> validatorList) {

}
