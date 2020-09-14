package net.croz.nrich.notification.service;

import net.croz.nrich.notification.api.model.AdditionalNotificationData;
import net.croz.nrich.notification.api.model.Notification;
import net.croz.nrich.notification.api.model.NotificationSeverity;
import net.croz.nrich.notification.api.model.ValidationFailureNotification;
import net.croz.nrich.notification.api.response.ResponseWithNotification;
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
        final NotificationResolverService notificationResolverService = mock(NotificationResolverService.class);

        when(notificationResolverService.createNotificationForAction(eq("actionPrefix.actionSuffix"), any(AdditionalNotificationData.class))).thenReturn(new Notification("title", "Action success", Collections.emptyList(), NotificationSeverity.INFO, Collections.emptyMap()));
        when(notificationResolverService.createNotificationForValidationFailure(any(Errors.class), any(), any(AdditionalNotificationData.class))).thenReturn(new ValidationFailureNotification("validation", "Validation failed 1", Collections.emptyList(), NotificationSeverity.INFO, Collections.emptyMap(), Collections.emptyList()));
        when(notificationResolverService.createNotificationForValidationFailure(any(ConstraintViolationException.class), any(AdditionalNotificationData.class))).thenReturn(new ValidationFailureNotification("validation", "Validation failed 2", Collections.emptyList(), NotificationSeverity.INFO, Collections.emptyMap(), Collections.emptyList()));
        when(notificationResolverService.createNotificationForException(any(Throwable.class), any(AdditionalNotificationData.class))).thenReturn(new Notification("title", "Exception", Collections.emptyList(), NotificationSeverity.ERROR, Collections.emptyMap()));

        notificationResponseService = new WebMvcNotificationResponseService(notificationResolverService);
    }

    @Test
    void shouldReturnNotificationResolverService() {
        // when
        final NotificationResolverService notificationResolverService = notificationResponseService.notificationResolverService();

        // then
        assertThat(notificationResolverService).isNotNull();
    }

    @Test
    void shouldCreateResponseForValidationFailure() {
        // when
        final ResponseWithNotification<?> responseWithNotification = notificationResponseService.responseWithValidationFailureNotification(mock(Errors.class), Object.class);

        // then
        assertThat(responseWithNotification).isNotNull();
        assertThat(responseWithNotification.getNotification()).isNotNull();
        assertThat(responseWithNotification.getNotification().getSeverity()).isEqualTo(NotificationSeverity.INFO);
        assertThat(responseWithNotification.getNotification().getContent()).isEqualTo("Validation failed 1");
    }

    @Test
    void shouldCreateResponseForValidationFailureForConstraintViolation() {
        // when
        final ResponseWithNotification<?> responseWithNotification = notificationResponseService.responseWithValidationFailureNotification(mock(ConstraintViolationException.class));

        // then
        assertThat(responseWithNotification).isNotNull();
        assertThat(responseWithNotification.getNotification()).isNotNull();
        assertThat(responseWithNotification.getNotification().getSeverity()).isEqualTo(NotificationSeverity.INFO);
        assertThat(responseWithNotification.getNotification().getContent()).isEqualTo("Validation failed 2");
    }

    @Test
    void shouldCreateResponseForException() {
        // when
        final ResponseWithNotification<?> responseWithNotification = notificationResponseService.responseWithExceptionNotification(new Exception());

        // then
        assertThat(responseWithNotification).isNotNull();
        assertThat(responseWithNotification.getNotification()).isNotNull();
        assertThat(responseWithNotification.getNotification().getSeverity()).isEqualTo(NotificationSeverity.ERROR);
        assertThat(responseWithNotification.getNotification().getContent()).isEqualTo("Exception");
    }

    @Test
    void shouldCreateNotificationFromCurrentRequest() {
        // given
        final String data = "data";
        final MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest("POST", "/actionPrefix/actionSuffix");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));

        // when
        final ResponseWithNotification<?> responseWithNotification = notificationResponseService.responseWithNotificationActionResolvedFromRequest(data);

        // then
        assertThat(responseWithNotification).isNotNull();
        assertThat(responseWithNotification.getData()).isEqualTo(data);
        assertThat(responseWithNotification.getNotification()).isNotNull();
        assertThat(responseWithNotification.getNotification().getSeverity()).isEqualTo(NotificationSeverity.INFO);
        assertThat(responseWithNotification.getNotification().getContent()).isEqualTo("Action success");
    }

    @Test
    void shouldCreateNotificationFromActionName() {
        // given
        final String data = "data";

        // when
        final ResponseWithNotification<?> responseWithNotification = notificationResponseService.responseWithNotification(data, "actionPrefix.actionSuffix");

        // then
        assertThat(responseWithNotification).isNotNull();
        assertThat(responseWithNotification.getData()).isEqualTo(data);
        assertThat(responseWithNotification.getNotification()).isNotNull();
        assertThat(responseWithNotification.getNotification().getSeverity()).isEqualTo(NotificationSeverity.INFO);
        assertThat(responseWithNotification.getNotification().getContent()).isEqualTo("Action success");
    }

    @Test
    void shouldCreateNotificationFromCurrentRequestWithoutData() {
        // given
        final MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest("POST", "/actionPrefix/actionSuffix");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));

        // when
        final ResponseWithNotification<?> responseWithNotification = notificationResponseService.responseWithNotificationActionResolvedFromRequest();

        // then
        assertThat(responseWithNotification).isNotNull();
        assertThat(responseWithNotification.getNotification()).isNotNull();
        assertThat(responseWithNotification.getNotification().getSeverity()).isEqualTo(NotificationSeverity.INFO);
        assertThat(responseWithNotification.getNotification().getContent()).isEqualTo("Action success");
    }

    @Test
    void shouldCreateNotificationFromActionNameWithoutData() {
        // when
        final ResponseWithNotification<?> responseWithNotification = notificationResponseService.responseWithNotification("actionPrefix.actionSuffix");

        // then
        assertThat(responseWithNotification).isNotNull();
        assertThat(responseWithNotification.getNotification()).isNotNull();
        assertThat(responseWithNotification.getNotification().getSeverity()).isEqualTo(NotificationSeverity.INFO);
        assertThat(responseWithNotification.getNotification().getContent()).isEqualTo("Action success");
    }

}
