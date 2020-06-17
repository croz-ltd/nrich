package net.croz.nrich.notification.service;

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
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WebMvcNotificationResponseServiceTest {

    private WebMvcNotificationResponseService notificationResponseService;

    @BeforeEach
    void setup() {
        final NotificationResolverService notificationResolverService = mock(NotificationResolverService.class);

        when(notificationResolverService.createNotificationForSuccessfulAction("actionPrefix.actionSuffix")).thenReturn(new Notification("title", "Action success", Collections.emptyList(), NotificationSeverity.INFO));
        when(notificationResolverService.createNotificationForValidationFailure(any(Errors.class), any())).thenReturn(new ValidationFailureNotification("validation", "Validation failed 1", Collections.emptyList(), NotificationSeverity.INFO, Collections.emptyList()));
        when(notificationResolverService.createNotificationForValidationFailure(any(ConstraintViolationException.class))).thenReturn(new ValidationFailureNotification("validation", "Validation failed 2", Collections.emptyList(), NotificationSeverity.INFO, Collections.emptyList()));
        when(notificationResolverService.createNotificationForException(any(Throwable.class), anyMap())).thenReturn(new Notification("title", "Exception", Collections.emptyList(), NotificationSeverity.ERROR));

        notificationResponseService = new WebMvcNotificationResponseService(notificationResolverService);
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
        final ResponseWithNotification<?> responseWithNotification = notificationResponseService.responseWithExceptionNotification(new Exception(), Collections.emptyMap());

        // then
        assertThat(responseWithNotification).isNotNull();
        assertThat(responseWithNotification.getNotification()).isNotNull();
        assertThat(responseWithNotification.getNotification().getSeverity()).isEqualTo(NotificationSeverity.ERROR);
        assertThat(responseWithNotification.getNotification().getContent()).isEqualTo("Exception");
    }

    @Test
    void shouldCreateSuccessNotificationFromCurrentRequest() {
        // given
        final String data = "data";
        final MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest("POST", "/actionPrefix/actionSuffix");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));

        // when
        final ResponseWithNotification<String> responseWithNotification = notificationResponseService.responseWithSuccessNotificationActionResolvedFromRequest(data);

        // then
        assertThat(responseWithNotification).isNotNull();
        assertThat(responseWithNotification.getData()).isEqualTo(data);
        assertThat(responseWithNotification.getNotification()).isNotNull();
        assertThat(responseWithNotification.getNotification().getSeverity()).isEqualTo(NotificationSeverity.INFO);
        assertThat(responseWithNotification.getNotification().getContent()).isEqualTo("Action success");
    }
}
