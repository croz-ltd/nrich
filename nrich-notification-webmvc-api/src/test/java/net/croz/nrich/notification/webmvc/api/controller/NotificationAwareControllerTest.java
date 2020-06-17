package net.croz.nrich.notification.webmvc.api.controller;

import net.croz.nrich.notification.api.model.Notification;
import net.croz.nrich.notification.api.model.NotificationSeverity;
import net.croz.nrich.notification.api.response.ResponseWithNotification;
import net.croz.nrich.notification.api.service.NotificationResolverService;
import net.croz.nrich.notification.webmvc.api.controller.stub.NotificationAwareControllerTestComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NotificationAwareControllerTest {

    private NotificationAwareControllerTestComponent notificationAwareControllerTestComponent;

    @BeforeEach
    void setup() {
        final NotificationResolverService notificationResolverService = mock(NotificationResolverService.class);

        when(notificationResolverService.createNotificationForSuccessfulAction("actionPrefix.actionSuffix")).thenReturn(new Notification("title", "Action success", Collections.emptyList(), NotificationSeverity.INFO));

        notificationAwareControllerTestComponent = new NotificationAwareControllerTestComponent(notificationResolverService);
    }

    @Test
    void shouldCreateSuccessNotificationFromCurrentRequest() {
        // given
        final String data = "data";
        final MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest("POST", "/actionPrefix/actionSuffix");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));

        // when
        final ResponseWithNotification<String> responseWithNotification = notificationAwareControllerTestComponent.responseWithSuccessNotificationActionResolvedFromRequest(data);

        // then
        assertThat(responseWithNotification).isNotNull();
        assertThat(responseWithNotification.getData()).isEqualTo(data);
        assertThat(responseWithNotification.getNotification()).isNotNull();
        assertThat(responseWithNotification.getNotification().getSeverity()).isEqualTo(NotificationSeverity.INFO);
        assertThat(responseWithNotification.getNotification().getContent()).isEqualTo("Action success");
    }
}
