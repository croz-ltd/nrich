package net.croz.nrich.notification.response;

import lombok.Data;
import net.croz.nrich.notification.model.Notification;

@Data
public class ResponseWithNotification<T> {

    private final T data;

    private final Notification notification;

}
