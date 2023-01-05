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

package net.croz.nrich.webmvc.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.croz.nrich.webmvc.WebmvcTestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringJUnitWebConfig(WebmvcTestConfiguration.class)
public abstract class BaseControllerTest {

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    protected MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    protected ResultActions performPostRequest(String requestUrl) throws Exception {
        return performPostRequest(requestUrl, Collections.emptyMap());
    }

    protected ResultActions performPostRequest(String requestUrl, Object requestData) throws Exception {
        return mockMvc.perform(post(requestUrl)
            .content(objectMapper.writeValueAsString(requestData))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));
    }

    protected ResultActions performFormPostRequest(String requestUrl) throws Exception {
        return performFormPostRequest(requestUrl, Collections.emptyMap());
    }

    protected ResultActions performFormPostRequest(String requestUrl, Map<String, String> requestData) throws Exception {
        return mockMvc.perform(post(requestUrl)
            .params(createFromMap(requestData))
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .accept(MediaType.APPLICATION_JSON));
    }

    private MultiValueMap<String, String> createFromMap(Map<String, String> requestData) {
        MultiValueMap<String, String> resultMap = new LinkedMultiValueMap<>();

        requestData.forEach((key, value) -> resultMap.put(key, Collections.singletonList(value)));

        return resultMap;
    }
}
