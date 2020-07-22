package net.croz.nrich.notification.api.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import net.croz.nrich.notification.api.model.Notification;

/**
 * Wrapper around response data that allows for sending notification with original response data.
 *
 * @param <T> type of response data
 */
@RequiredArgsConstructor
@Getter
public class ResponseWithNotification<T> {

    /**
     * Response data
     */
    private final T data;

    /**
     * Notification
     */
    private final Notification notification;

}
