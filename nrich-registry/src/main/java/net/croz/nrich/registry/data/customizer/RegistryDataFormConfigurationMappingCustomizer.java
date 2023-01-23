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

package net.croz.nrich.registry.data.customizer;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.formconfiguration.api.customizer.FormConfigurationMappingCustomizer;
import net.croz.nrich.registry.api.core.service.RegistryClassResolvingService;
import net.croz.nrich.registry.core.constants.RegistryClassResolvingConstants;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class RegistryDataFormConfigurationMappingCustomizer implements FormConfigurationMappingCustomizer {

    private final RegistryClassResolvingService registryClassResolvingService;

    private final List<Class<?>> registryClassList;

    @Override
    public void customizeConfigurationMapping(Map<String, Class<?>> formConfigurationMapping) {
        registerRegistryFormConfiguration(formConfigurationMapping);
    }

    private void registerRegistryFormConfiguration(Map<String, Class<?>> formConfigurationMap) {
        registryClassList.forEach(registryClass -> {
            String registryClassName = registryClass.getName();
            String registryCreateFormId = String.format(RegistryClassResolvingConstants.REGISTRY_FORM_ID_FORMAT, registryClassName, RegistryClassResolvingConstants.REGISTRY_FORM_ID_CREATE_SUFFIX);
            String registryUpdateFormId = String.format(RegistryClassResolvingConstants.REGISTRY_FORM_ID_FORMAT, registryClassName, RegistryClassResolvingConstants.REGISTRY_FORM_ID_UPDATE_SUFFIX);

            formConfigurationMap.computeIfAbsent(registryCreateFormId, key -> registryClassResolvingService.resolveCreateClass(registryClassName));
            formConfigurationMap.computeIfAbsent(registryUpdateFormId, key -> registryClassResolvingService.resolveUpdateClass(registryClassName));
        });
    }
}
