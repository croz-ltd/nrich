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

package net.croz.nrich.formconfiguration.api.util;

import net.croz.nrich.formconfiguration.api.customizer.FormConfigurationMappingCustomizer;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class FormConfigurationMappingCustomizerUtil {

    private FormConfigurationMappingCustomizerUtil() {
    }

    public static Map<String, Class<?>> applyCustomizerList(Map<String, Class<?>> formConfiguration, List<FormConfigurationMappingCustomizer> formConfigurationCustomizerList) {
        List<FormConfigurationMappingCustomizer> resolvedCustomizerList = Optional.ofNullable(formConfigurationCustomizerList).orElse(Collections.emptyList());
        Map<String, Class<?>> formConfigurationMapping = new LinkedHashMap<>(Optional.ofNullable(formConfiguration).orElse(new LinkedHashMap<>()));

        resolvedCustomizerList.forEach(formConfigurationMappingCustomizer -> formConfigurationMappingCustomizer.customizeConfigurationMapping(formConfigurationMapping));

        return formConfigurationMapping;
    }
}
