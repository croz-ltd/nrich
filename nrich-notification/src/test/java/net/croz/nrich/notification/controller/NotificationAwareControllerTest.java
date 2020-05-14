package net.croz.nrich.notification.controller;

import net.croz.nrich.notification.NotificationTestConfiguration;
import net.croz.nrich.notification.model.NotificationSeverity;
import net.croz.nrich.notification.response.ResponseWithNotification;
import net.croz.nrich.notification.stub.NotificationAwareControllerTestComponent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = NotificationTestConfiguration.class)
public class NotificationAwareControllerTest {

    @Autowired
    private NotificationAwareControllerTestComponent notificationAwareControllerTestComponent;

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
