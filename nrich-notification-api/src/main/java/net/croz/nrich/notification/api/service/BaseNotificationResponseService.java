package net.croz.nrich.notification.api.service;

import net.croz.nrich.notification.api.model.AdditionalNotificationData;
import net.croz.nrich.notification.api.response.NotificationDataResponse;
import org.springframework.validation.Errors;

import javax.validation.ConstraintViolationException;


/**
 * Helper service that helps creation response with notification.
 *
 * @param <T> Type of response with notification i.e. {@link NotificationDataResponse}
 */
public interface BaseNotificationResponseService<T> {

    /**
     * Returns response with {@link net.croz.nrich.notification.api.model.ValidationFailureNotification} instance.
     *
     * @param errors                     Springs {@link Errors} that will be used to resolve validation notification messages.
     * @param validationFailedOwningType class on which validation errors were found
     * @param additionalNotificationData additional notification data to add to notification
     * @return response with validation failure notification
     */
    T responseWithValidationFailureNotification(Errors errors, Class<?> validationFailedOwningType, AdditionalNotificationData additionalNotificationData);

    /**
     * Returns response with {@link net.croz.nrich.notification.api.model.ValidationFailureNotification} instance.
     *
     * @param exception                  validation exception that will be used to resolve validation notification messages.
     * @param additionalNotificationData additional notification data to add to notification
     * @return response with validation failure notification
     */
    T responseWithValidationFailureNotification(ConstraintViolationException exception, AdditionalNotificationData additionalNotificationData);

    /**
     * Returns response with {@link net.croz.nrich.notification.api.model.Notification} instance.
     *
     * @param throwable                    exception for which to resolve notification
     * @param additionalNotificationData   additional notification data to add to notification
     * @param exceptionMessageArgumentList optional exception argument list, will be used when resolving exception message
     * @return response with notification
     */
    T responseWithExceptionNotification(Throwable throwable, AdditionalNotificationData additionalNotificationData, Object... exceptionMessageArgumentList);

    /**
     * Returns response with {@link net.croz.nrich.notification.api.model.Notification} instance.
     *
     * @param data                       data to include in response
     * @param additionalNotificationData additional notification data to add to notification
     * @param <D>                        type of data
     * @return response with notification
     */
    <D> T responseWithNotificationActionResolvedFromRequest(D data, AdditionalNotificationData additionalNotificationData);

    /**
     * Returns response with {@link net.croz.nrich.notification.api.model.Notification} instance.
     *
     * @param data                       data to include in response
     * @param actionName                 name of the action for which to resolve notification
     * @param additionalNotificationData additional notification data to add to notification
     * @param <D>                        type of data
     * @return response with notification
     */
    <D> T responseWithNotification(D data, String actionName, AdditionalNotificationData additionalNotificationData);

    NotificationResolverService notificationResolverService();

    default T responseWithValidationFailureNotification(Errors errors, Class<?> validationFailedOwningType) {
        return responseWithValidationFailureNotification(errors, validationFailedOwningType, AdditionalNotificationData.empty());
    }

    default T responseWithValidationFailureNotification(ConstraintViolationException exception) {
        return responseWithValidationFailureNotification(exception, AdditionalNotificationData.empty());
    }

    default T responseWithExceptionNotification(Throwable throwable, Object... additionalMessageArgumentList) {
        return responseWithExceptionNotification(throwable, AdditionalNotificationData.empty(), additionalMessageArgumentList);
    }

    default <D> T responseWithNotificationActionResolvedFromRequest(D data) {
        return responseWithNotificationActionResolvedFromRequest(data, AdditionalNotificationData.empty());
    }

    default <D> T responseWithNotification(D data, String actionName) {
        return responseWithNotification(data, actionName, AdditionalNotificationData.empty());
    }

    default T responseWithNotificationActionResolvedFromRequest() {
        return responseWithNotificationActionResolvedFromRequest(null, AdditionalNotificationData.empty());
    }

    default T responseWithNotification(String actionName) {
        return responseWithNotification(null, actionName, AdditionalNotificationData.empty());
    }
}
