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

package net.croz.nrich.problemdetail.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.croz.nrich.problemdetail.ProblemDetailTestConfiguration;
import net.croz.nrich.problemdetail.constant.ProblemDetailConstants;
import net.croz.nrich.problemdetail.handler.stub.NrichProblemDetailExceptionHandlerTestException;
import net.croz.nrich.problemdetail.handler.stub.NrichProblemDetailExceptionHandlerTestExceptionWithoutMessageCode;
import net.croz.nrich.problemdetail.handler.stub.NrichProblemDetailExceptionHandlerTestRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitWebConfig(ProblemDetailTestConfiguration.class)
class NrichProblemDetailExceptionHandlerTest {

    private static final String STATUS_PATH = "$.status";

    private static final String TYPE_PATH = "$.type";

    private static final String TITLE_PATH = "$.title";

    private static final String DETAIL_PATH = "$.detail";

    private static final String CODE_PATH = "$." + ProblemDetailConstants.CODE_PROPERTY;

    private static final String SEVERITY_PATH = "$." + ProblemDetailConstants.SEVERITY_PROPERTY;

    private static final String ERROR_ID_PATH = "$." + ProblemDetailConstants.ERROR_ID_PROPERTY;

    private static final String TIMESTAMP_PATH = "$." + ProblemDetailConstants.TIMESTAMP_PROPERTY;

    private static final String ERRORS_PATH = "$." + ProblemDetailConstants.ERRORS_PROPERTY;

    private static final String FIRST_ERROR_CODE_PATH = ERRORS_PATH + "[0].code";

    private static final String FIRST_ERROR_FIELD_PATH = ERRORS_PATH + "[0].field";

    private static final String FIRST_ERROR_REJECTED_VALUE_PATH = ERRORS_PATH + "[0].rejectedValue";

    private static final String FIRST_ERROR_MESSAGE_PATH = ERRORS_PATH + "[0].message";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void shouldHandleGenericException() throws Exception {
        // given
        String requestUrl = fullUrl("generic-exception");

        // when
        ResultActions result = performPostRequest(requestUrl);

        // then
        result.andExpect(status().isInternalServerError())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath(STATUS_PATH).value(500))
            .andExpect(jsonPath(TYPE_PATH).value("about:blank"))
            .andExpect(jsonPath(TITLE_PATH).value("Error"))
            .andExpect(jsonPath(DETAIL_PATH).value(ProblemDetailConstants.DEFAULT_ERROR_DETAIL))
            .andExpect(jsonPath(SEVERITY_PATH).value(ProblemDetailConstants.SEVERITY_ERROR))
            .andExpect(jsonPath(ERROR_ID_PATH).value(notNullValue()))
            .andExpect(jsonPath(TIMESTAMP_PATH).value(notNullValue()));
    }

    @Test
    void shouldResolveDetailFromMessageSourceForExceptionWithMessageCode() throws Exception {
        // given
        String requestUrl = fullUrl("exception-with-message-code");

        // when
        ResultActions result = performPostRequest(requestUrl);

        // then
        result.andExpect(status().isInternalServerError())
            .andExpect(jsonPath(TYPE_PATH).value("urn:problem:order-not-found"))
            .andExpect(jsonPath(DETAIL_PATH).value("Order ORD-001 could not be located"))
            .andExpect(jsonPath(CODE_PATH).value(NrichProblemDetailExceptionHandlerTestException.MESSAGE_CODE))
            .andExpect(jsonPath(SEVERITY_PATH).value(ProblemDetailConstants.SEVERITY_WARNING));
    }

    @Test
    void shouldFallBackToExceptionMessageWhenMessageSourceHasNoMatch() throws Exception {
        // given
        String requestUrl = fullUrl("exception-without-message-code");

        // when
        ResultActions result = performPostRequest(requestUrl);

        // then
        result.andExpect(status().isInternalServerError())
            .andExpect(jsonPath(DETAIL_PATH).value(NrichProblemDetailExceptionHandlerTestExceptionWithoutMessageCode.MESSAGE));
    }

    @Test
    void shouldResolveDetailWithFrameworkExceptionArguments() throws Exception {
        // given
        String requestUrl = fullUrl("generic-exception");

        // when
        ResultActions result = mockMvc.perform(get(requestUrl).accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isMethodNotAllowed())
            .andExpect(jsonPath(STATUS_PATH).value(405))
            .andExpect(jsonPath(TYPE_PATH).value("urn:problem:method-not-allowed"))
            .andExpect(jsonPath(DETAIL_PATH).value("Method GET is not supported."));
    }

    @Test
    void shouldHonorResponseStatusAnnotation() throws Exception {
        // given
        String requestUrl = fullUrl("response-status-exception");

        // when
        ResultActions result = performPostRequest(requestUrl);

        // then
        result.andExpect(status().isNotFound())
            .andExpect(jsonPath(STATUS_PATH).value(404));
    }

    @Test
    void shouldOverrideStatusFromMessageSource() throws Exception {
        // given
        String requestUrl = fullUrl("status-override-exception");

        // when
        ResultActions result = performPostRequest(requestUrl);

        // then
        result.andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath(STATUS_PATH).value(422))
            .andExpect(jsonPath(CODE_PATH).value("status-override"));
    }

    @Test
    void shouldFallBackToDefaultStatusWhenConfiguredStatusIsInvalid() throws Exception {
        // given
        String requestUrl = fullUrl("invalid-status-override-exception");

        // when
        ResultActions result = performPostRequest(requestUrl);

        // then
        result.andExpect(status().isInternalServerError())
            .andExpect(jsonPath(STATUS_PATH).value(500))
            .andExpect(jsonPath(DETAIL_PATH).value(ProblemDetailConstants.DEFAULT_ERROR_DETAIL));
    }

    @Test
    void shouldHandleConstraintViolation() throws Exception {
        // given
        String requestUrl = fullUrl("constraint-violation");

        // when
        ResultActions result = performPostRequest(requestUrl);

        // then
        result.andExpect(status().isBadRequest())
            .andExpect(jsonPath(STATUS_PATH).value(400))
            .andExpect(jsonPath(TITLE_PATH).value("Validation failed"))
            .andExpect(jsonPath(DETAIL_PATH).value(ProblemDetailConstants.DEFAULT_VALIDATION_DETAIL))
            .andExpect(jsonPath(FIRST_ERROR_CODE_PATH).value("NotNull"))
            .andExpect(jsonPath(FIRST_ERROR_FIELD_PATH).value("name"))
            .andExpect(jsonPath(FIRST_ERROR_MESSAGE_PATH).value("Name really cannot be null"));
    }

    @Test
    void shouldHandleMethodArgumentNotValid() throws Exception {
        // given
        String requestUrl = fullUrl("method-argument-not-valid");

        // when
        ResultActions result = performPostRequest(requestUrl, new NrichProblemDetailExceptionHandlerTestRequest());

        // then
        result.andExpect(status().isBadRequest())
            .andExpect(jsonPath(STATUS_PATH).value(400))
            .andExpect(jsonPath(DETAIL_PATH).value(ProblemDetailConstants.DEFAULT_VALIDATION_DETAIL))
            .andExpect(jsonPath(FIRST_ERROR_CODE_PATH).value("NotNull"))
            .andExpect(jsonPath(FIRST_ERROR_FIELD_PATH).value("name"))
            .andExpect(jsonPath(FIRST_ERROR_MESSAGE_PATH).value("Name really cannot be null"));
    }

    @Test
    void shouldHandleBindValidationFailed() throws Exception {
        // given
        String requestUrl = fullUrl("bind-validation-failed-resolving");

        // when
        ResultActions result = performFormPostRequest(requestUrl);

        // then
        result.andExpect(status().isBadRequest())
            .andExpect(jsonPath(STATUS_PATH).value(400))
            .andExpect(jsonPath(FIRST_ERROR_CODE_PATH).value("NotNull"))
            .andExpect(jsonPath(FIRST_ERROR_FIELD_PATH).value("name"))
            .andExpect(jsonPath(FIRST_ERROR_MESSAGE_PATH).value("Name really cannot be null"));
    }

    @Test
    void shouldHandleMethodParameterValidation() throws Exception {
        // given
        String requestUrl = fullUrl("method-parameter-validation") + "?name=a";

        // when
        ResultActions result = performPostRequest(requestUrl);

        // then
        result.andExpect(status().isBadRequest())
            .andExpect(jsonPath(STATUS_PATH).value(400))
            .andExpect(jsonPath(FIRST_ERROR_CODE_PATH).value("Size"))
            .andExpect(jsonPath(FIRST_ERROR_FIELD_PATH).value("name"))
            .andExpect(jsonPath(FIRST_ERROR_REJECTED_VALUE_PATH).value("a"))
            .andExpect(jsonPath(FIRST_ERROR_MESSAGE_PATH).value("Name must have at least 2 characters"));
    }

    @Test
    void shouldUnwrapWrappedExceptionAndRouteThroughCauseType() throws Exception {
        // given
        String requestUrl = fullUrl("unwrap-generic-exception");

        // when
        ResultActions result = performPostRequest(requestUrl);

        // then
        result.andExpect(status().isInternalServerError())
            .andExpect(jsonPath(DETAIL_PATH).value("Order ORD-001 could not be located"))
            .andExpect(jsonPath(CODE_PATH).value(NrichProblemDetailExceptionHandlerTestException.MESSAGE_CODE));
    }

    @ValueSource(strings = { "unwrap-constraint-violation", "unwrap-method-argument-not-valid" })
    @ParameterizedTest
    void shouldUnwrapBadRequestExceptions(String url) throws Exception {
        // given
        String requestUrl = fullUrl(url);

        // when
        ResultActions result = performPostRequest(requestUrl);

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotUnwrapWhenWrapperTypeIsNotConfigured() throws Exception {
        // given
        String requestUrl = fullUrl("not-unwrapped-wrapper");

        // when
        ResultActions result = performPostRequest(requestUrl);

        // then
        result.andExpect(status().isInternalServerError())
            .andExpect(jsonPath(STATUS_PATH).value(500))
            .andExpect(jsonPath(DETAIL_PATH).value(ProblemDetailConstants.DEFAULT_ERROR_DETAIL));
    }

    private ResultActions performPostRequest(String requestUrl) throws Exception {
        return performPostRequest(requestUrl, Collections.emptyMap());
    }

    private ResultActions performPostRequest(String requestUrl, Object requestData) throws Exception {
        return mockMvc.perform(post(requestUrl)
            .content(objectMapper.writeValueAsString(requestData))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));
    }

    private ResultActions performFormPostRequest(String requestUrl) throws Exception {
        return performFormPostRequest(requestUrl, Collections.emptyMap());
    }

    private ResultActions performFormPostRequest(String requestUrl, Map<String, String> requestData) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        requestData.forEach((key, value) -> params.put(key, List.of(value)));

        return mockMvc.perform(post(requestUrl)
            .params(params)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .accept(MediaType.APPLICATION_JSON));
    }

    private String fullUrl(String path) {
        return String.format("/problem-detail-test-controller/%s", path);
    }
}
