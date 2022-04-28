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

package net.croz.nrich.registry.data.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.croz.nrich.registry.core.model.RegistryDataConfigurationHolder;
import net.croz.nrich.registry.data.constant.RegistryDataConstants;
import net.croz.nrich.registry.data.request.CreateRegistryRequest;
import net.croz.nrich.registry.data.request.UpdateRegistryRequest;
import net.croz.nrich.registry.data.util.ClassLoadingUtil;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class DefaultRegistryDataRequestConversionService implements RegistryDataRequestConversionService {

    private final ObjectMapper objectMapper;

    private final RegistryDataConfigurationHolder registryDataConfigurationHolder;

    @Override
    public Object convertEntityDataToTyped(CreateRegistryRequest request) {
        Class<?> type = resolveClassWithConfigurationVerification(request.getClassFullName(), RegistryDataConstants.CREATE_REQUEST_SUFFIX);

        return convertStringToInstance(request.getJsonEntityData(), type);
    }

    @Override
    public Object convertEntityDataToTyped(UpdateRegistryRequest request) {
        Class<?> type = resolveClassWithConfigurationVerification(request.getClassFullName(), RegistryDataConstants.UPDATE_REQUEST_SUFFIX);

        return convertStringToInstance(request.getJsonEntityData(), type);
    }

    private Class<?> resolveClassWithConfigurationVerification(String classFullName, String classLoadingInitialPrefix) {
        registryDataConfigurationHolder.verifyConfigurationExists(classFullName);

        List<String> classNameList = Arrays.asList(String.format(classLoadingInitialPrefix, classFullName), String.format(RegistryDataConstants.REQUEST_SUFFIX, classFullName), classFullName);

        return ClassLoadingUtil.loadClassFromList(classNameList);
    }

    @SneakyThrows
    private Object convertStringToInstance(String entityData, Class<?> entityType) {
        return objectMapper.readValue(entityData, entityType);
    }
}
