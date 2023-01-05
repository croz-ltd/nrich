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

package net.croz.nrich.registry.configuration.controller;

import net.croz.nrich.registry.test.BaseControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RegistryConfigurationControllerTest extends BaseControllerTest {

    private static final String REQUEST_URL = "/nrich/registry/configuration/fetch";

    @Test
    void shouldFetchRegistryConfiguration() throws Exception {
        // when
        ResultActions result = performPostRequest(REQUEST_URL);

        // then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void shouldSerializeJavascriptTypeAsLowerCase() throws Exception {
        // when
        ResultActions result = performPostRequest(REQUEST_URL);

        // then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("[*]['entityConfigurationList'][*]['propertyConfigurationList'][*]['javascriptType']",
                hasItems("string", "number", "object")));
    }
}
