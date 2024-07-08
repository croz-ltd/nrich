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

package net.croz.nrich.registry.core.service;

import net.croz.nrich.registry.api.core.service.RegistryClassResolvingService;
import net.croz.nrich.registry.core.constants.RegistryClassResolvingConstants;
import net.croz.nrich.registry.core.constants.RegistryCoreConstants;
import net.croz.nrich.registry.core.model.RegistryDataConfigurationHolder;
import net.croz.nrich.registry.data.util.ClassLoadingUtil;
import org.springframework.cache.annotation.Cacheable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DefaultRegistryClassResolvingService implements RegistryClassResolvingService {

    private final RegistryDataConfigurationHolder registryDataConfigurationHolder;

    private final Map<String, Class<?>> createClassMapping;

    private final Map<String, Class<?>> updateClassMapping;

    public DefaultRegistryClassResolvingService(RegistryDataConfigurationHolder registryDataConfigurationHolder, Map<String, Class<?>> createClassMapping, Map<String, Class<?>> updateClassMapping) {
        this.registryDataConfigurationHolder = registryDataConfigurationHolder;
        this.createClassMapping = Optional.ofNullable(createClassMapping).orElse(Collections.emptyMap());
        this.updateClassMapping = Optional.ofNullable(updateClassMapping).orElse(Collections.emptyMap());
    }

    @Cacheable("nrich.classResolvingCreate.cache")
    @Override
    public Class<?> resolveCreateClass(String registryClassName) {
        registryDataConfigurationHolder.verifyConfigurationExists(registryClassName);

        if (createClassMapping.containsKey(registryClassName)) {
            return createClassMapping.get(registryClassName);
        }

        return resolveClassByPackage(registryClassName, RegistryClassResolvingConstants.CREATE_REQUEST_CLASS_NAME_FORMAT);
    }

    @Cacheable("nrich.classResolvingUpdate.cache")
    @Override
    public Class<?> resolveUpdateClass(String registryClassName) {
        registryDataConfigurationHolder.verifyConfigurationExists(registryClassName);

        if (updateClassMapping.containsKey(registryClassName)) {
            return updateClassMapping.get(registryClassName);
        }

        return resolveClassByPackage(registryClassName, RegistryClassResolvingConstants.UPDATE_REQUEST_CLASS_NAME_FORMAT);
    }

    private Class<?> resolveClassByPackage(String registryFullClassName, String requestClassNameFormat) {
        List<String> registryClassNameList = new ArrayList<>(List.of(registryFullClassName.split(RegistryClassResolvingConstants.PACKAGE_SEPARATOR)));

        int indexOfLastPackage = registryClassNameList.size() - 2;
        if (RegistryClassResolvingConstants.CLASS_NAME_SUFFIX_LIST_TO_REPLACE.contains(registryClassNameList.get(indexOfLastPackage))) {
            registryClassNameList.remove(registryClassNameList.get(indexOfLastPackage));
        }

        registryClassNameList.add(registryClassNameList.size() - 1, RegistryClassResolvingConstants.REQUEST_CLASS_PACKAGE_NAME);

        String fullClassNameWithRequestPackage = String.join(RegistryCoreConstants.DOT, registryClassNameList);

        return resolveClass(registryFullClassName, fullClassNameWithRequestPackage, requestClassNameFormat);
    }

    private Class<?> resolveClass(String fullClassName, String fullClassNameWithRequestPackage, String requestClassNameFormat) {
        List<String> classNameList = List.of(
            String.format(requestClassNameFormat, fullClassNameWithRequestPackage),
            String.format(RegistryClassResolvingConstants.REQUEST_CLASS_NAME_FORMAT, fullClassNameWithRequestPackage),
            String.format(requestClassNameFormat, fullClassName),
            String.format(RegistryClassResolvingConstants.REQUEST_CLASS_NAME_FORMAT, fullClassName),
            fullClassName
        );

        return ClassLoadingUtil.loadClassFromList(classNameList);
    }
}
