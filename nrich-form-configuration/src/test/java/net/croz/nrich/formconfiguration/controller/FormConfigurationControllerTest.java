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

package net.croz.nrich.formconfiguration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.croz.nrich.formconfiguration.api.model.FormConfiguration;
import net.croz.nrich.formconfiguration.api.request.FetchFormConfigurationRequest;
import net.croz.nrich.formconfiguration.api.service.FormConfigurationService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static net.croz.nrich.formconfiguration.testutil.FormConfigurationGeneratingUtil.createFetchFormConfigurationRequest;
import static net.croz.nrich.formconfiguration.testutil.FormConfigurationGeneratingUtil.createFormConfiguration;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FormConfigurationControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private FormConfigurationService formConfigurationService;

    @InjectMocks
    private FormConfigurationController formConfigurationController;

    @CsvSource({ ",/nrich/form/configuration/fetch", "/api/form/configuration,/api/form/configuration/fetch" })
    @ParameterizedTest
    void shouldReturnFormConfiguration(String endpointPath, String uri) throws Exception {
        // given
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(formConfigurationController).addPlaceholderValue("nrich.form-configuration.endpoint-path", endpointPath).build();

        FetchFormConfigurationRequest request = createFetchFormConfigurationRequest();
        FormConfiguration formConfiguration = createFormConfiguration();

        doReturn(Collections.singletonList(formConfiguration)).when(formConfigurationService).fetchFormConfigurationList(request.getFormIdList());

        // when
        ResultActions result = mockMvc.perform(post(uri)
            .content(objectMapper.writeValueAsString(request))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$[*].formId").value(formConfiguration.getFormId()));
    }
}
