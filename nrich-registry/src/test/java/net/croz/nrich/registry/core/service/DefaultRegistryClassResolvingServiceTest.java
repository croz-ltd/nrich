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

import net.croz.nrich.registry.core.model.RegistryDataConfigurationHolder;
import net.croz.nrich.registry.core.stub.RegistryClassLoadingSecondTestEntity;
import net.croz.nrich.registry.core.stub.RegistryClassLoadingTestEntity;
import net.croz.nrich.registry.core.stub.entity.RegistryClassLoadingThirdTestEntity;
import net.croz.nrich.registry.core.stub.request.RegistryClassLoadingTestEntityCreateRequest;
import net.croz.nrich.registry.core.stub.request.RegistryClassLoadingTestEntityUpdateRequest;
import net.croz.nrich.registry.core.stub.request.RegistryClassLoadingThirdTestEntityCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DefaultRegistryClassResolvingServiceTest {

    @Mock
    private RegistryDataConfigurationHolder registryDataConfigurationHolder;

    private DefaultRegistryClassResolvingService defaultRegistryClassResolvingService;

    @BeforeEach
    void setup() {
        Map<String, Class<?>> createClassMapping = Collections.singletonMap("net.croz.create.TestEntity", Object.class);
        Map<String, Class<?>> updateClassMapping = Collections.singletonMap("net.croz.update.TestEntity", Object.class);

        defaultRegistryClassResolvingService = new DefaultRegistryClassResolvingService(registryDataConfigurationHolder, createClassMapping, updateClassMapping);
    }

    @Test
    void shouldNotFailWithEmptyMappings() {
        // given
        defaultRegistryClassResolvingService = new DefaultRegistryClassResolvingService(registryDataConfigurationHolder, null, null);

        // when
        Throwable thrown = catchThrowable(() -> {
            defaultRegistryClassResolvingService.resolveCreateClass(RegistryClassLoadingThirdTestEntity.class.getName());
            defaultRegistryClassResolvingService.resolveUpdateClass(RegistryClassLoadingThirdTestEntity.class.getName());

        });

        // then
        assertThat(thrown).isNull();
    }

    @Test
    void shouldResolveClassFromCreateMapWhenPresent() {
        // given
        String classFullName = "net.croz.create.TestEntity";

        // when
        Class<?> result = defaultRegistryClassResolvingService.resolveCreateClass(classFullName);

        // then
        assertThat(result).isEqualTo(Object.class);
        verify(registryDataConfigurationHolder).verifyConfigurationExists(classFullName);
    }

    @Test
    void shouldLoadCreateClassFromRequestPackageWhenEntityIsInEntityPackage() {
        // given
        String classFullName = RegistryClassLoadingThirdTestEntity.class.getName();

        // when
        Class<?> result = defaultRegistryClassResolvingService.resolveCreateClass(classFullName);

        // then
        assertThat(result).isEqualTo(RegistryClassLoadingThirdTestEntityCreateRequest.class);
        verify(registryDataConfigurationHolder).verifyConfigurationExists(classFullName);
    }

    @Test
    void shouldLoadCreateClassFromRequestPackage() {
        // given
        String classFullName = RegistryClassLoadingTestEntity.class.getName();

        // when
        Class<?> result = defaultRegistryClassResolvingService.resolveCreateClass(classFullName);

        // then
        assertThat(result).isEqualTo(RegistryClassLoadingTestEntityCreateRequest.class);
        verify(registryDataConfigurationHolder).verifyConfigurationExists(classFullName);
    }

    @Test
    void shouldLoadCreateClassFromModelPackage() {
        // given
        String classFullName = RegistryClassLoadingSecondTestEntity.class.getName();

        // when
        Class<?> result = defaultRegistryClassResolvingService.resolveCreateClass(classFullName);

        // then
        assertThat(result).isEqualTo(RegistryClassLoadingSecondTestEntity.class);
        verify(registryDataConfigurationHolder).verifyConfigurationExists(classFullName);
    }

    @Test
    void shouldResolveClassFromUpdateMapWhenPresent() {
        // given
        String classFullName = "net.croz.update.TestEntity";

        // when
        Class<?> result = defaultRegistryClassResolvingService.resolveUpdateClass(classFullName);

        // then
        assertThat(result).isEqualTo(Object.class);
        verify(registryDataConfigurationHolder).verifyConfigurationExists(classFullName);
    }

    @Test
    void shouldLoadUpdateClassFromRequestPackage() {
        // given
        String classFullName = RegistryClassLoadingTestEntity.class.getName();

        // when
        Class<?> result = defaultRegistryClassResolvingService.resolveUpdateClass(classFullName);

        // then
        assertThat(result).isEqualTo(RegistryClassLoadingTestEntityUpdateRequest.class);
        verify(registryDataConfigurationHolder).verifyConfigurationExists(classFullName);
    }

    @Test
    void shouldLoadUpdateClassFromModelPackage() {
        // given
        String classFullName = RegistryClassLoadingSecondTestEntity.class.getName();

        // when
        Class<?> result = defaultRegistryClassResolvingService.resolveUpdateClass(classFullName);

        // then
        assertThat(result).isEqualTo(RegistryClassLoadingSecondTestEntity.class);
        verify(registryDataConfigurationHolder).verifyConfigurationExists(classFullName);
    }
}
