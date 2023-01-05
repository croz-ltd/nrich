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

package net.croz.nrich.webmvc.advice;

import net.croz.nrich.webmvc.test.BaseControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.WebDataBinder;

import java.util.Map;

import static net.croz.nrich.webmvc.advice.testutil.MapGeneratingUtil.createMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ControllerEditorRegistrationAdviceTest extends BaseControllerTest {

    @Test
    void shouldNotRegisterBindersWhenDisabled() {
        // given
        WebDataBinder webDataBinder = mock(WebDataBinder.class);
        ControllerEditorRegistrationAdvice controllerEditorRegistrationAdvice = new ControllerEditorRegistrationAdvice(false, false, null);

        // when
        controllerEditorRegistrationAdvice.initBinder(webDataBinder);

        // then
        verifyNoInteractions(webDataBinder);
    }

    @Test
    void shouldConvertEmptyStringsToNull() throws Exception {
        String requestUrl = fullUrl("empty-strings-to-null");
        Map<String, String> request = createMap("nonEmptyString", "value", "emptyString", "");

        // when
        ResultActions result = performFormPostRequest(requestUrl, request);

        // then
        result.andExpect(status().isOk())
            .andExpect(content().string("value=null"));
    }

    @Test
    void shouldNotBindTransientFields() throws Exception {
        // given
        String requestUrl = fullUrl("transient-properties-serialization");
        String value = "value";
        Map<String, String> request = createMap("transientProperty", "transient", "property", "nonTransient");

        // when
        ResultActions result = performFormPostRequest(requestUrl, request);

        // then
        result.andExpect(status().isOk())
            .andExpect(content().string("value=nonTransient transientValue=null"));
    }

    private String fullUrl(String path) {
        return String.format("/controller-editor-registration-advice-test-controller/%s", path);
    }
}
