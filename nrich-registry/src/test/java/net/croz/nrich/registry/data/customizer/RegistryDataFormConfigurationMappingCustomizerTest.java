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

import net.croz.nrich.registry.data.constant.RegistryDataConstants;
import net.croz.nrich.registry.data.stub.RegistryTestEntity;
import net.croz.nrich.registry.data.stub.RegistryTestEntityUpdateRequest;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithOverriddenFormConfiguration;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.croz.nrich.registry.data.testutil.RegistryDataMapGeneratingUtil.createFormConfigurationMap;
import static org.assertj.core.api.Assertions.assertThat;

class RegistryDataFormConfigurationMappingCustomizerTest {

    private static final List<Class<?>> REGISTRY_CLASS_LIST = Arrays.asList(RegistryTestEntity.class, RegistryTestEntityWithOverriddenFormConfiguration.class);

    private final RegistryDataFormConfigurationMappingCustomizer formConfigurationMappingCustomizer = new RegistryDataFormConfigurationMappingCustomizer(REGISTRY_CLASS_LIST);

    @Test
    void shouldResolveRegistryFormConfigurationForEntityWithoutCreateRequest() {
        // given
        Class<?> entity = RegistryTestEntity.class;
        String formId = String.format(RegistryDataConstants.REGISTRY_FORM_ID_FORMAT, entity.getName(), RegistryDataConstants.REGISTRY_FORM_ID_CREATE_SUFFIX);
        Map<String, Class<?>> formConfiguration = new HashMap<>();

        // when
        formConfigurationMappingCustomizer.customizeConfigurationMapping(formConfiguration);

        // and when
        Class<?> registryCreateClass = formConfiguration.get(formId);

        // then
        assertThat(registryCreateClass).isEqualTo(RegistryTestEntity.class);
    }

    @Test
    void shouldResolveRegistryFormConfigurationForEntityWithoutUpdateRequest() {
        // given
        Class<?> entity = RegistryTestEntity.class;
        String formId = String.format(RegistryDataConstants.REGISTRY_FORM_ID_FORMAT, entity.getName(), RegistryDataConstants.REGISTRY_FORM_ID_UPDATE_SUFFIX);
        Map<String, Class<?>> formConfiguration = new HashMap<>();

        // when
        formConfigurationMappingCustomizer.customizeConfigurationMapping(formConfiguration);

        // and when
        Class<?> registryCreateClass = formConfiguration.get(formId);

        // then
        assertThat(registryCreateClass).isEqualTo(RegistryTestEntityUpdateRequest.class);
    }

    @Test
    void shouldNotAddFormConfigurationForExistingCreateRequest() {
        // given
        Class<?> entity = RegistryTestEntity.class;
        String formId = String.format(RegistryDataConstants.REGISTRY_FORM_ID_FORMAT, entity.getName(), RegistryDataConstants.REGISTRY_FORM_ID_CREATE_SUFFIX);
        Map<String, Class<?>> formConfiguration = createFormConfigurationMap(formId, Object.class);

        // when
        formConfigurationMappingCustomizer.customizeConfigurationMapping(formConfiguration);

        // and when
        Class<?> registryCreateClass = formConfiguration.get(formId);

        // then
        assertThat(registryCreateClass).isEqualTo(Object.class);
    }

    @Test
    void shouldNotAddFormConfigurationForExistingUpdateRequest() {
        // given
        Class<?> entity = RegistryTestEntity.class;
        String formId = String.format(RegistryDataConstants.REGISTRY_FORM_ID_FORMAT, entity.getName(), RegistryDataConstants.REGISTRY_FORM_ID_UPDATE_SUFFIX);
        Map<String, Class<?>> formConfiguration = createFormConfigurationMap(formId, Object.class);

        // when
        formConfigurationMappingCustomizer.customizeConfigurationMapping(formConfiguration);

        // and when
        Class<?> registryCreateClass = formConfiguration.get(formId);

        // then
        assertThat(registryCreateClass).isEqualTo(Object.class);
    }
}
