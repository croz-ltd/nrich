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

package net.croz.nrich.formconfiguration.api.service;

import java.util.List;
import java.util.Map;

/**
 * Resolves {@link net.croz.nrich.formconfiguration.api.annotation.FormValidationConfiguration} annotated classes as a Map to be used by
 * {@link FormConfigurationService}.
 */
public interface FormConfigurationAnnotationResolvingService {

    /**
     * {@link net.croz.nrich.formconfiguration.api.annotation.FormValidationConfiguration} annotated classes as a Map.
     *
     * @param packageList packages to scan for annotated classes
     * @return map of formIds with classes
     */
    Map<String, Class<?>> resolveFormConfigurations(List<String> packageList);

}
