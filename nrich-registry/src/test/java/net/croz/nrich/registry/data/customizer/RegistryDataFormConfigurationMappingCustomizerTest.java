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

import net.croz.nrich.registry.api.core.service.RegistryClassResolvingService;
import net.croz.nrich.registry.core.constants.RegistryClassResolvingConstants;
import net.croz.nrich.registry.data.stub.RegistryTestEntity;
import net.croz.nrich.registry.data.stub.RegistryTestEntityUpdateRequest;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithOverriddenFormConfiguration;
import net.croz.nrich.registry.data.stub.model.RegistryCustomizerTestEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.croz.nrich.registry.data.testutil.RegistryDataMapGeneratingUtil.createFormConfigurationMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class RegistryDataFormConfigurationMappingCustomizerTest {

    private static final List<Class<?>> REGISTRY_CLASS_LIST = Arrays.asList(
        RegistryTestEntity.class, RegistryTestEntityWithOverriddenFormConfiguration.class, RegistryCustomizerTestEntity.class
    );

    @Mock
    private RegistryClassResolvingService registryClassResolvingService;

    private RegistryDataFormConfigurationMappingCustomizer formConfigurationMappingCustomizer;

    @BeforeEach
    void setup() {
        formConfigurationMappingCustomizer = new RegistryDataFormConfigurationMappingCustomizer(registryClassResolvingService, REGISTRY_CLASS_LIST);
    }

    @Test
    void shouldResolveRegistryFormConfigurationForEntityWithoutCreateRequest() {
        // given
        String classFullName = RegistryTestEntity.class.getName();
        String formId = String.format(RegistryClassResolvingConstants.REGISTRY_FORM_ID_FORMAT, classFullName, RegistryClassResolvingConstants.REGISTRY_FORM_ID_CREATE_SUFFIX);
        Map<String, Class<?>> formConfiguration = new HashMap<>();
        doReturn(RegistryTestEntity.class).when(registryClassResolvingService).resolveCreateClass(classFullName);

        // when
        formConfigurationMappingCustomizer.customizeConfigurationMapping(formConfiguration);

        // then
        assertThat(formConfiguration).containsEntry(formId, RegistryTestEntity.class);
    }

    @Test
    void shouldResolveRegistryFormConfigurationForEntityWithoutUpdateRequest() {
        // given
        String classFullName = RegistryTestEntity.class.getName();
        String formId = String.format(RegistryClassResolvingConstants.REGISTRY_FORM_ID_FORMAT, classFullName, RegistryClassResolvingConstants.REGISTRY_FORM_ID_UPDATE_SUFFIX);
        Map<String, Class<?>> formConfiguration = new HashMap<>();
        doReturn(RegistryTestEntity.class).when(registryClassResolvingService).resolveUpdateClass(classFullName);

        // when
        formConfigurationMappingCustomizer.customizeConfigurationMapping(formConfiguration);

        // then
        assertThat(formConfiguration).containsEntry(formId, RegistryTestEntity.class);
    }

    @Test
    void shouldNotAddFormConfigurationForExistingCreateRequest() {
        // given
        String classFullName = RegistryTestEntity.class.getName();
        String formId = String.format(RegistryClassResolvingConstants.REGISTRY_FORM_ID_FORMAT, classFullName, RegistryClassResolvingConstants.REGISTRY_FORM_ID_CREATE_SUFFIX);
        Map<String, Class<?>> formConfiguration = createFormConfigurationMap(formId, Object.class);

        // when
        formConfigurationMappingCustomizer.customizeConfigurationMapping(formConfiguration);

        // then
        assertThat(formConfiguration).containsEntry(formId, Object.class);
    }

    @Test
    void shouldNotAddFormConfigurationForExistingUpdateRequest() {
        // given
        String classFullName = RegistryTestEntity.class.getName();
        String formId = String.format(RegistryClassResolvingConstants.REGISTRY_FORM_ID_FORMAT, classFullName, RegistryClassResolvingConstants.REGISTRY_FORM_ID_UPDATE_SUFFIX);
        Map<String, Class<?>> formConfiguration = createFormConfigurationMap(formId, Object.class);

        // when
        formConfigurationMappingCustomizer.customizeConfigurationMapping(formConfiguration);

        // then
        assertThat(formConfiguration).containsEntry(formId, Object.class);
    }
}
