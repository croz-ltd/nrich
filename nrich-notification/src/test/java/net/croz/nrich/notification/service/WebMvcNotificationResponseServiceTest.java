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

package net.croz.nrich.notification.service;

import net.croz.nrich.notification.api.model.AdditionalNotificationData;
import net.croz.nrich.notification.api.model.Notification;
import net.croz.nrich.notification.api.model.NotificationSeverity;
import net.croz.nrich.notification.api.model.ValidationFailureNotification;
import net.croz.nrich.notification.api.response.NotificationDataResponse;
import net.croz.nrich.notification.api.response.NotificationResponse;
import net.croz.nrich.notification.api.service.NotificationResolverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.Errors;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.validation.ConstraintViolationException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WebMvcNotificationResponseServiceTest {

    private WebMvcNotificationResponseService notificationResponseService;

    @BeforeEach
    void setup() {
        NotificationResolverService notificationResolverService = mock(NotificationResolverService.class);
        Notification firstNotification = new Notification("title", "Action success", Collections.emptyList(), NotificationSeverity.INFO, Collections.emptyMap());
        Notification secondNotification = new Notification("title", "Exception", Collections.emptyList(), NotificationSeverity.ERROR, Collections.emptyMap());
        ValidationFailureNotification firstValidationNotification = new ValidationFailureNotification(
            "validation", "Validation failed 1", Collections.emptyList(), NotificationSeverity.INFO, Collections.emptyMap(), Collections.emptyList()
        );
        ValidationFailureNotification secondValidationNotification = new ValidationFailureNotification(
            "validation", "Validation failed 2", Collections.emptyList(), NotificationSeverity.INFO, Collections.emptyMap(), Collections.emptyList()
        );

        when(notificationResolverService.createNotificationForAction(eq("actionPrefix.actionSuffix"), any(AdditionalNotificationData.class))).thenReturn(firstNotification);
        when(notificationResolverService.createNotificationForAction(eq("actionPrefix.actionSuffix.post"), any(AdditionalNotificationData.class))).thenReturn(firstNotification);
        when(notificationResolverService.createNotificationForValidationFailure(any(Errors.class), any(), any(AdditionalNotificationData.class))).thenReturn(firstValidationNotification);
        when(notificationResolverService.createNotificationForValidationFailure(any(ConstraintViolationException.class), any(AdditionalNotificationData.class))).thenReturn(secondValidationNotification);
        when(notificationResolverService.createNotificationForException(any(Throwable.class), any(AdditionalNotificationData.class))).thenReturn(secondNotification);

        notificationResponseService = new WebMvcNotificationResponseService(notificationResolverService);
    }

    @Test
    void shouldReturnNotificationResolverService() {
        // when
        NotificationResolverService notificationResolverService = notificationResponseService.notificationResolverService();

        // then
        assertThat(notificationResolverService).isNotNull();
    }

    @Test
    void shouldCreateResponseForValidationFailure() {
        // when
        NotificationResponse notificationResponse = notificationResponseService.responseWithValidationFailureNotification(mock(Errors.class), Object.class);

        // then
        assertThat(notificationResponse).isNotNull();
        assertThat(notificationResponse.getNotification()).isNotNull();
        assertThat(notificationResponse.getNotification().getSeverity()).isEqualTo(NotificationSeverity.INFO);
        assertThat(notificationResponse.getNotification().getContent()).isEqualTo("Validation failed 1");
    }

    @Test
    void shouldCreateResponseForValidationFailureForConstraintViolation() {
        // when
        NotificationResponse notificationResponse = notificationResponseService.responseWithValidationFailureNotification(mock(ConstraintViolationException.class));

        // then
        assertThat(notificationResponse).isNotNull();
        assertThat(notificationResponse.getNotification()).isNotNull();
        assertThat(notificationResponse.getNotification().getSeverity()).isEqualTo(NotificationSeverity.INFO);
        assertThat(notificationResponse.getNotification().getContent()).isEqualTo("Validation failed 2");
    }

    @Test
    void shouldCreateResponseForException() {
        // when
        NotificationResponse notificationResponse = notificationResponseService.responseWithExceptionNotification(new Exception());

        // then
        assertThat(notificationResponse).isNotNull();
        assertThat(notificationResponse.getNotification()).isNotNull();
        assertThat(notificationResponse.getNotification().getSeverity()).isEqualTo(NotificationSeverity.ERROR);
        assertThat(notificationResponse.getNotification().getContent()).isEqualTo("Exception");
    }

    @Test
    void shouldCreateNotificationFromCurrentRequest() {
        // given
        String data = "data";
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest("POST", "/actionPrefix/actionSuffix");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));

        // when
        NotificationDataResponse<String> notificationDataResponse = notificationResponseService.responseWithNotificationActionResolvedFromRequest(data);

        // then
        assertThat(notificationDataResponse).isNotNull();
        assertThat(notificationDataResponse.getData()).isEqualTo(data);
        assertThat(notificationDataResponse.getNotification()).isNotNull();
        assertThat(notificationDataResponse.getNotification().getSeverity()).isEqualTo(NotificationSeverity.INFO);
        assertThat(notificationDataResponse.getNotification().getContent()).isEqualTo("Action success");
    }

    @Test
    void shouldCreateNotificationFromActionName() {
        // given
        String data = "data";

        // when
        NotificationDataResponse<String> notificationDataResponse = notificationResponseService.responseWithNotification(data, "actionPrefix.actionSuffix");

        // then
        assertThat(notificationDataResponse).isNotNull();
        assertThat(notificationDataResponse.getData()).isEqualTo(data);
        assertThat(notificationDataResponse.getNotification()).isNotNull();
        assertThat(notificationDataResponse.getNotification().getSeverity()).isEqualTo(NotificationSeverity.INFO);
        assertThat(notificationDataResponse.getNotification().getContent()).isEqualTo("Action success");
    }

    @Test
    void shouldCreateNotificationFromCurrentRequestWithoutData() {
        // given
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest("POST", "/actionPrefix/actionSuffix");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));

        // when
        NotificationResponse notificationDataResponse = notificationResponseService.responseWithNotificationActionResolvedFromRequest();

        // then
        assertThat(notificationDataResponse).isNotNull();
        assertThat(notificationDataResponse.getNotification()).isNotNull();
        assertThat(notificationDataResponse.getNotification().getSeverity()).isEqualTo(NotificationSeverity.INFO);
        assertThat(notificationDataResponse.getNotification().getContent()).isEqualTo("Action success");
    }

    @Test
    void shouldCreateNotificationFromActionNameWithoutData() {
        // when
        NotificationResponse notificationDataResponse = notificationResponseService.responseWithNotification("actionPrefix.actionSuffix");

        // then
        assertThat(notificationDataResponse).isNotNull();
        assertThat(notificationDataResponse.getNotification()).isNotNull();
        assertThat(notificationDataResponse.getNotification().getSeverity()).isEqualTo(NotificationSeverity.INFO);
        assertThat(notificationDataResponse.getNotification().getContent()).isEqualTo("Action success");
    }
}
