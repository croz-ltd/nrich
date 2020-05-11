package net.croz.nrich.notification.controller;

import net.croz.nrich.notification.model.Notification;
import net.croz.nrich.notification.response.ResponseWithNotification;
import net.croz.nrich.notification.service.NotificationResolverService;

public interface NotificationAwareController {

    default <T> ResponseWithNotification<T> responseWithSuccessNotificationActionResolvedFromRequest(final T data) {
        final String actionName = NotificationAwareControllerUtil.extractActionNameFromCurrentRequest();

        return responseWithSuccessNotification(data, actionName);
    }

    default <T> ResponseWithNotification<T> responseWithSuccessNotification(final T data, final String actionName) {
        final Notification notification = getNotificationResolverService().createNotificationForSuccessfulAction(actionName);

        return new ResponseWithNotification<>(data, notification);
    }

    NotificationResolverService getNotificationResolverService();
}
