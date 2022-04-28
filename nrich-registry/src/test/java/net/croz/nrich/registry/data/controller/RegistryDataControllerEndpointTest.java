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

package net.croz.nrich.registry.data.controller;

import net.croz.nrich.registry.api.data.request.ListRegistryRequest;
import net.croz.nrich.registry.data.stub.RegistryTestEntity;
import net.croz.nrich.registry.test.BaseControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.ResultActions;

import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createListRegistryRequest;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = "nrich.registry.data.endpoint-path=api/registry/data")
class RegistryDataControllerEndpointTest extends BaseControllerTest {

    @Test
    void shouldListRegistryOnCustomDomain() throws Exception {
        // given
        String requestUrl = "/api/registry/data/list";
        ListRegistryRequest request = createListRegistryRequest(RegistryTestEntity.class.getName(), "name%");

        // when
        ResultActions result = performPostRequest(requestUrl, request);

        // then
        result.andExpect(status().isOk());
    }
}
