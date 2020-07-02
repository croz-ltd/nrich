package net.croz.nrich.notification.api.service;

import net.croz.nrich.notification.api.model.AdditionalNotificationData;
import org.springframework.validation.Errors;

import javax.validation.ConstraintViolationException;

// TODO check if additional method are needed
public interface NotificationResponseService<T> {

    T responseWithValidationFailureNotification(Errors errors, Class<?> validationFailedOwningType, AdditionalNotificationData additionalNotificationData);

    T responseWithValidationFailureNotification(ConstraintViolationException exception, AdditionalNotificationData additionalNotificationData);

    T responseWithExceptionNotification(Throwable throwable, AdditionalNotificationData additionalNotificationData, Object... additionalMessageArgumentList);

    <D> T responseWithNotificationActionResolvedFromRequest(D data, AdditionalNotificationData additionalNotificationData);

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
