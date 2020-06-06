package net.croz.nrich.notification.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.notification.model.Notification;

@RequiredArgsConstructor
@Getter
public class ResponseWithNotification<T> {

    private final T data;

    private final Notification notification;

}
