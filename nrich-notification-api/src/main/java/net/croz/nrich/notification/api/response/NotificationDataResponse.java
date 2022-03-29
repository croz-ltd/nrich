package net.croz.nrich.notification.api.response;

import lombok.Getter;
import net.croz.nrich.notification.api.model.Notification;

/**
 * Wrapper around response data that allows for sending notification with original response data.
 *
 * @param <T> type of response data
 */
@Getter
public class NotificationDataResponse<T> extends NotificationResponse {

    /**
     * Response data
     */
    private final T data;

    public NotificationDataResponse(Notification notification, T data) {
        super(notification);
        this.data = data;
    }
}
