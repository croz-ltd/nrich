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

package net.croz.nrich.registry.data.customizer;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.formconfiguration.api.customizer.FormConfigurationMappingCustomizer;
import net.croz.nrich.registry.core.constants.RegistryCoreConstants;
import net.croz.nrich.registry.data.constant.RegistryDataConstants;
import net.croz.nrich.registry.data.util.ClassLoadingUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class RegistryDataFormConfigurationMappingCustomizer implements FormConfigurationMappingCustomizer {

    private final List<Class<?>> registryClassList;

    @Override
    public void customizeConfigurationMapping(Map<String, Class<?>> formConfigurationMapping) {
        registerRegistryFormConfiguration(formConfigurationMapping);
    }

    private void registerRegistryFormConfiguration(Map<String, Class<?>> formConfigurationMap) {
        registryClassList.forEach(registryClass -> {
            String registryClassName = registryClass.getName();
            String registryCreateFormId = String.format(RegistryDataConstants.REGISTRY_FORM_ID_FORMAT, registryClassName, RegistryDataConstants.REGISTRY_FORM_ID_CREATE_SUFFIX);
            String registryUpdateFormId = String.format(RegistryDataConstants.REGISTRY_FORM_ID_FORMAT, registryClassName, RegistryDataConstants.REGISTRY_FORM_ID_UPDATE_SUFFIX);

            formConfigurationMap.computeIfAbsent(registryCreateFormId, key -> resolveClassByPackage(registryClassName, RegistryDataConstants.CREATE_REQUEST_CLASS_NAME_FORMAT));
            formConfigurationMap.computeIfAbsent(registryUpdateFormId, key -> resolveClassByPackage(registryClassName, RegistryDataConstants.UPDATE_REQUEST_CLASS_NAME_FORMAT));
        });
    }

    private Class<?> resolveClassByPackage(String registryFullClassName, String requestClassNameFormat) {
        List<String> registryClassNameList = new ArrayList<>(Arrays.asList(registryFullClassName.split(RegistryDataConstants.PACKAGE_SEPARATOR)));

        int indexOfLastPackage = registryClassNameList.size() - 2;
        if (RegistryDataConstants.CLASS_NAME_SUFFIX_LIST_TO_REPLACE.contains(registryClassNameList.get(indexOfLastPackage))) {
            registryClassNameList.remove(registryClassNameList.get(indexOfLastPackage));
        }

        registryClassNameList.add(registryClassNameList.size() - 1, RegistryDataConstants.REQUEST_CLASS_PACKAGE_NAME);

        String fullClassNameWithRequestPackage = String.join(RegistryCoreConstants.DOT, registryClassNameList);

        return resolveClass(registryFullClassName, fullClassNameWithRequestPackage, requestClassNameFormat);
    }

    private Class<?> resolveClass(String fullClassName, String fullClassNameWithRequestPackage, String requestClassNameFormat) {
        List<String> classNameList = Arrays.asList(
            String.format(requestClassNameFormat, fullClassNameWithRequestPackage),
            String.format(RegistryDataConstants.REQUEST_CLASS_NAME_FORMAT, fullClassNameWithRequestPackage),
            String.format(requestClassNameFormat, fullClassName),
            String.format(RegistryDataConstants.REQUEST_CLASS_NAME_FORMAT, fullClassName),
            fullClassName
        );

        return ClassLoadingUtil.loadClassFromList(classNameList);
    }
}
