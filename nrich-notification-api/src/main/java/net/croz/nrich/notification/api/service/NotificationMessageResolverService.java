package net.croz.nrich.notification.api.service;

import net.croz.nrich.notification.api.model.NotificationMessageResolvingData;

public interface NotificationMessageResolverService {

    String resolveMessage(NotificationMessageResolvingData notificationMessageResolvingData);

}
