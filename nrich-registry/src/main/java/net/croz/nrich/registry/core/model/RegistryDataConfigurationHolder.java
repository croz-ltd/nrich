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

package net.croz.nrich.registry.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.registry.core.support.ManagedTypeWrapper;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class RegistryDataConfigurationHolder {

    private final Map<String, ManagedTypeWrapper> classNameManagedTypeWrapperMap;

    private final List<RegistryDataConfiguration<Object, Object>> registryDataConfigurationList;

    public void verifyConfigurationExists(String classFullName) {
        findRegistryConfigurationForClass(classFullName);
    }

    public RegistryDataConfiguration<Object, Object> findRegistryConfigurationForClass(String classFullName) {
        return registryDataConfigurationList.stream()
            .filter(configuration -> configuration.getRegistryType().getName().equals(classFullName))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("Configuration for registry entity %s is not defined!", classFullName)));
    }

    public ManagedTypeWrapper resolveManagedTypeWrapper(String className) {
        return classNameManagedTypeWrapperMap.get(className);
    }
}
