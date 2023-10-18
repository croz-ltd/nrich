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

package net.croz.nrich.registry.enumadata.controller;

import net.croz.nrich.registry.api.enumdata.request.ListBulkRegistryEnumRequest;
import net.croz.nrich.registry.api.enumdata.request.ListRegistryEnumRequest;
import net.croz.nrich.registry.enumadata.stub.RegistryTestEnum;
import net.croz.nrich.registry.enumadata.stub.RegistryTestEnumWithAdditionalData;
import net.croz.nrich.registry.test.BaseControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import static net.croz.nrich.registry.enumadata.testutil.RegistryEnumGeneratingUtil.createBulkListEnumRequest;
import static net.croz.nrich.registry.enumadata.testutil.RegistryEnumGeneratingUtil.createListEnumRequest;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RegistryEnumControllerTest extends BaseControllerTest {

    @Test
    void shouldBulkListRegistry() throws Exception {
        // given
        String classFullName = RegistryTestEnum.class.getName();
        String requestUrl = fullUrl("list-bulk");
        ListBulkRegistryEnumRequest request = createBulkListEnumRequest(classFullName, "First");

        // when
        ResultActions result = performPostRequest(requestUrl, request);

        // then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.['" + classFullName + "']").isNotEmpty());
    }

    @Test
    void shouldListRegistry() throws Exception {
        // given
        String classFullName = RegistryTestEnumWithAdditionalData.class.getName();
        String requestUrl = fullUrl("list");
        ListRegistryEnumRequest request = createListEnumRequest(classFullName, "Second");

        // when
        ResultActions result = performPostRequest(requestUrl, request);

        // then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    private String fullUrl(String path) {
        return String.format("/nrich/registry/enum/%s", path);
    }
}
