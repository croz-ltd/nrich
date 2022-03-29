package net.croz.nrich.notification.api.service;

import net.croz.nrich.notification.api.model.AdditionalNotificationData;
import net.croz.nrich.notification.api.response.NotificationDataResponse;
import net.croz.nrich.notification.api.response.NotificationResponse;

public interface NotificationResponseService extends BaseNotificationResponseService<NotificationResponse> {

    /**
     * Returns response with {@link net.croz.nrich.notification.api.model.Notification} instance.
     *
     * @param data                       data to include in response
     * @param additionalNotificationData additional notification data to add to notification
     * @param <D>                        type of data
     * @return response with notification
     */
    <D> NotificationDataResponse<D> responseWithNotificationActionResolvedFromRequest(D data, AdditionalNotificationData additionalNotificationData);

    /**
     * Returns response with {@link net.croz.nrich.notification.api.model.Notification} instance.
     *
     * @param data                       data to include in response
     * @param actionName                 name of the action for which to resolve notification
     * @param additionalNotificationData additional notification data to add to notification
     * @param <D>                        type of data
     * @return response with notification
     */
    <D> NotificationDataResponse<D> responseWithNotification(D data, String actionName, AdditionalNotificationData additionalNotificationData);

    default <D> NotificationDataResponse<D> responseWithNotificationActionResolvedFromRequest(D data) {
        return responseWithNotificationActionResolvedFromRequest(data, AdditionalNotificationData.empty());
    }

    default <D> NotificationDataResponse<D> responseWithNotification(D data, String actionName) {
        return responseWithNotification(data, actionName, AdditionalNotificationData.empty());
    }
}
