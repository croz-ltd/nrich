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

package net.croz.nrich.registry.security.interceptor;

import net.croz.nrich.registry.api.core.model.RegistryOverrideConfiguration;
import net.croz.nrich.registry.api.data.interceptor.BaseRegistryDataInterceptor;
import net.croz.nrich.registry.api.security.exception.RegistryUpdateNotAllowedException;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class RegistryConfigurationUpdateInterceptor extends BaseRegistryDataInterceptor {

    private final Map<String, RegistryOverrideConfiguration> registryOverrideConfigurationMap;

    public RegistryConfigurationUpdateInterceptor(Map<Class<?>, RegistryOverrideConfiguration> registryOverrideConfigurationMap) {
        this.registryOverrideConfigurationMap = initializeRegistryOverrideConfigurationMap(registryOverrideConfigurationMap);
    }

    @Override
    public void beforeRegistryCreate(String classFullName, Object entityData) {
        RegistryOverrideConfiguration registryOverrideConfiguration = resolveConfiguration(classFullName);

        verifyRegistryOperation(classFullName, registryOverrideConfiguration.isReadOnly() || !registryOverrideConfiguration.isCreatable());
    }

    @Override
    public void beforeRegistryUpdate(String classFullName, Object id, Object entityData) {
        RegistryOverrideConfiguration registryOverrideConfiguration = resolveConfiguration(classFullName);

        verifyRegistryOperation(classFullName, registryOverrideConfiguration.isReadOnly() || !registryOverrideConfiguration.isUpdateable());
    }

    @Override
    public void beforeRegistryDelete(String classFullName, Object id) {
        RegistryOverrideConfiguration registryOverrideConfiguration = resolveConfiguration(classFullName);

        verifyRegistryOperation(classFullName, registryOverrideConfiguration.isReadOnly() || !registryOverrideConfiguration.isDeletable());
    }

    private RegistryOverrideConfiguration resolveConfiguration(String classFullName) {
        if (registryOverrideConfigurationMap.get(classFullName) == null) {
            return RegistryOverrideConfiguration.defaultConfiguration();
        }

        return registryOverrideConfigurationMap.get(classFullName);
    }

    private Map<String, RegistryOverrideConfiguration> initializeRegistryOverrideConfigurationMap(Map<Class<?>, RegistryOverrideConfiguration> registryOverrideConfigurationMap) {
        if (registryOverrideConfigurationMap == null) {
            return Collections.emptyMap();
        }

        return registryOverrideConfigurationMap.entrySet().stream()
            .collect(Collectors.toMap(entry -> entry.getKey().getName(), Map.Entry::getValue));
    }

    private void verifyRegistryOperation(String registryClassName, boolean isAllowed) {
        if (isAllowed) {
            throw new RegistryUpdateNotAllowedException(String.format("Trying to update registry: %s that is not updatable", registryClassName));
        }
    }
}
