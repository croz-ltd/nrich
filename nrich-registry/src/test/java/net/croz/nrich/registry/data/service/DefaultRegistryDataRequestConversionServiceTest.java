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
import net.croz.nrich.registry.core.model.RegistryDataConfigurationHolder;
import net.croz.nrich.registry.data.request.CreateRegistryRequest;
import net.croz.nrich.registry.data.request.UpdateRegistryRequest;
import net.croz.nrich.registry.data.stub.DefaultRegistryDataRequestConversionServiceTestEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static net.croz.nrich.registry.data.testutil.RegistryRequestGeneratingUtil.createCreateRegistryRequest;
import static net.croz.nrich.registry.data.testutil.RegistryRequestGeneratingUtil.createUpdateRegistryRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DefaultRegistryDataRequestConversionServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RegistryDataConfigurationHolder registryDataConfigurationHolder;

    @InjectMocks
    private DefaultRegistryDataRequestConversionService defaultRegistryDataRequestConversionService;

    @Test
    void shouldConvertCreateRequestDataToTypedInstance() throws Exception {
        // given
        DefaultRegistryDataRequestConversionServiceTestEntity entity = new DefaultRegistryDataRequestConversionServiceTestEntity();
        CreateRegistryRequest request = createCreateRegistryRequest(entity.getClass());

        doReturn(entity).when(objectMapper).readValue(request.getJsonEntityData(), entity.getClass());

        // when
        Object result = defaultRegistryDataRequestConversionService.convertEntityDataToTyped(request);

        // then
        assertThat(result).isEqualTo(entity);
        verify(registryDataConfigurationHolder).verifyConfigurationExists(request.getClassFullName());
    }

    @Test
    void shouldConvertUpdateRequestDataToTypedInstance() throws Exception {
        // given
        DefaultRegistryDataRequestConversionServiceTestEntity entity = new DefaultRegistryDataRequestConversionServiceTestEntity();
        UpdateRegistryRequest request = createUpdateRegistryRequest(entity.getClass());

        doReturn(entity).when(objectMapper).readValue(request.getJsonEntityData(), entity.getClass());

        // when
        Object result = defaultRegistryDataRequestConversionService.convertEntityDataToTyped(request);

        // then
        assertThat(result).isEqualTo(entity);
        verify(registryDataConfigurationHolder).verifyConfigurationExists(request.getClassFullName());
    }
}
