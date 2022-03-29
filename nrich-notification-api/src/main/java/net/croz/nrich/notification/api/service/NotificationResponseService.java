package net.croz.nrich.notification.api.service;

import net.croz.nrich.notification.api.model.AdditionalNotificationData;
import net.croz.nrich.notification.api.response.NotificationDataResponse;
import net.croz.nrich.notification.api.response.NotificationResponse;

public interface NotificationResponseService extends BaseNotificationResponseService<NotificationResponse> {

    @Override
    <D> NotificationDataResponse<D> responseWithNotificationActionResolvedFromRequest(D data, AdditionalNotificationData additionalNotificationData);

    @Override
    <D> NotificationDataResponse<D> responseWithNotification(D data, String actionName, AdditionalNotificationData additionalNotificationData);

    @Override
    default <D> NotificationDataResponse<D> responseWithNotificationActionResolvedFromRequest(D data) {
        return responseWithNotificationActionResolvedFromRequest(data, AdditionalNotificationData.empty());
    }

    @Override
    default <D> NotificationDataResponse<D> responseWithNotification(D data, String actionName) {
        return responseWithNotification(data, actionName, AdditionalNotificationData.empty());
    }
}
