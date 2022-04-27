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

package net.croz.nrich.webmvc.advice;

import net.croz.nrich.webmvc.test.BaseControllerTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NotificationErrorHandlingRestControllerAdviceTest extends BaseControllerTest {

    private static final String NOTIFICATION_TITLE_PATH = "$.notification.title";

    private static final String NOTIFICATION_CONTENT_PATH = "$.notification.content";

    private static final String NOTIFICATION_MESSAGE_LIST_PATH = "$.notification.messageList[0]";

    private static final String NOTIFICATION_VALIDATION_ERROR_LIST_PATH = "$.notification.validationErrorList[0]";

    @Test
    void shouldResolveExceptionNotification() throws Exception {
        // given
        String requestUrl = fullUrl("exception-resolving");

        // when
        ResultActions result = performPostRequest(requestUrl);

        // then
        result.andExpect(status().isBadGateway())
            .andExpect(jsonPath(NOTIFICATION_TITLE_PATH).value("Error"))
            .andExpect(jsonPath(NOTIFICATION_CONTENT_PATH).value("Error message"))
            .andExpect(jsonPath(NOTIFICATION_MESSAGE_LIST_PATH).value(startsWith("UUID")));
    }

    @Test
    void shouldResolveExceptionWithArgumentsNotification() throws Exception {
        // given
        String requestUrl = fullUrl("exception-resolving-with-arguments");

        // when
        ResultActions result = performPostRequest(requestUrl);

        // then
        result.andExpect(status().is5xxServerError())
            .andExpect(jsonPath(NOTIFICATION_TITLE_PATH).value("Error"))
            .andExpect(jsonPath(NOTIFICATION_CONTENT_PATH).value("Error message with arguments: 1"));
    }

    @Test
    void shouldResolveValidationNotification() throws Exception {
        // given
        String requestUrl = fullUrl("validation-failed-resolving");

        // when
        ResultActions result = performPostRequest(requestUrl);

        // then
        result.andExpect(status().is4xxClientError())
            .andExpect(jsonPath(NOTIFICATION_TITLE_PATH).value("Validation failed"))
            .andExpect(jsonPath(NOTIFICATION_CONTENT_PATH).value("Found validation errors:"))
            .andExpect(jsonPath(NOTIFICATION_MESSAGE_LIST_PATH).value("name: Name really cannot be null"))
            .andExpect(jsonPath(NOTIFICATION_VALIDATION_ERROR_LIST_PATH).isNotEmpty());
    }

    @Test
    void shouldResolveValidationBindFailedNotification() throws Exception {
        // given
        String requestUrl = fullUrl("bind-validation-failed-resolving");

        // when
        ResultActions result = performFormPostRequest(requestUrl);

        // then
        result.andExpect(status().is4xxClientError())
            .andExpect(jsonPath(NOTIFICATION_TITLE_PATH).value("Validation failed"))
            .andExpect(jsonPath(NOTIFICATION_CONTENT_PATH).value("Found validation errors:"))
            .andExpect(jsonPath(NOTIFICATION_MESSAGE_LIST_PATH).value("name: Name really cannot be null"))
            .andExpect(jsonPath(NOTIFICATION_VALIDATION_ERROR_LIST_PATH).isNotEmpty());
    }

    @Test
    void shouldUnwrapException() throws Exception {
        // given
        String requestUrl = fullUrl("unwrapped-exception-resolving");

        // when
        ResultActions result = performPostRequest(requestUrl);

        // then
        result.andExpect(status().isBadGateway())
            .andExpect(jsonPath(NOTIFICATION_CONTENT_PATH).value("Error message"))
            .andExpect(jsonPath(NOTIFICATION_MESSAGE_LIST_PATH).value(startsWith("UUID")));

    }

    @ValueSource(strings = { "unwrapped-exception-validation-failed-resolving", "unwrapped-exception-bind-exception-resolving", "unwrapped-exception-constraint-violation-exception-resolving" })
    @ParameterizedTest
    void shouldUnwrapBadRequestExceptions(String url) throws Exception {
        // given
        String requestUrl = fullUrl(url);

        // when
        ResultActions result = performFormPostRequest(requestUrl);

        // then
        result.andExpect(status().is4xxClientError());
    }

    @Test
    void shouldResolveConstraintExceptionNotification() throws Exception {
        // given
        String requestUrl = fullUrl("constraint-violation-exception-resolving");

        // when
        ResultActions result = performPostRequest(requestUrl);

        // then
        result.andExpect(status().is4xxClientError())
            .andExpect(jsonPath(NOTIFICATION_TITLE_PATH).value("Validation failed"))
            .andExpect(jsonPath(NOTIFICATION_CONTENT_PATH).value("Found validation errors:"))
            .andExpect(jsonPath(NOTIFICATION_MESSAGE_LIST_PATH).value("name: Name really cannot be null"))
            .andExpect(jsonPath(NOTIFICATION_VALIDATION_ERROR_LIST_PATH).isNotEmpty());
    }

    @Test
    void shouldResolveStatusForUnwrappedException() throws Exception {
        // given
        String requestUrl = fullUrl("unwrapped-exception-status-resolving");

        // when
        ResultActions result = performPostRequest(requestUrl);

        // then
        result.andExpect(status().isBadRequest());
    }

    private String fullUrl(String path) {
        return String.format("/notification-error-handling-test-controller/%s", path);
    }
}
