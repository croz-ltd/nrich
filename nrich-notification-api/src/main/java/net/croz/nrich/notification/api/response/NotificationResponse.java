package net.croz.nrich.notification.api.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.notification.api.model.Notification;

/**
 * Response that holds notification.
 */
@RequiredArgsConstructor
@Getter
public class NotificationResponse {

    /**
     * Notification
     */
    private final Notification notification;

}
