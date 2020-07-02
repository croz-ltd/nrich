package net.croz.nrich.notification.api.service;

import net.croz.nrich.notification.api.model.AdditionalNotificationData;
import net.croz.nrich.notification.api.model.Notification;
import net.croz.nrich.notification.api.model.ValidationFailureNotification;
import org.springframework.validation.Errors;

import javax.validation.ConstraintViolationException;

public interface NotificationResolverService {

    ValidationFailureNotification createNotificationForValidationFailure(Errors errors, Class<?> validationFailedOwningType, AdditionalNotificationData additionalNotificationData);

    ValidationFailureNotification createNotificationForValidationFailure(ConstraintViolationException exception, AdditionalNotificationData additionalNotificationData);

    Notification createNotificationForException(Throwable throwable, AdditionalNotificationData additionalNotificationData, Object... exceptionMessageArgumentList);

    Notification createNotificationForAction(String actionName, AdditionalNotificationData additionalNotificationData);

    default ValidationFailureNotification createNotificationForValidationFailure(Errors errors, Class<?> validationFailedOwningType) {
        return createNotificationForValidationFailure(errors, validationFailedOwningType, AdditionalNotificationData.empty());
    }

    default ValidationFailureNotification createNotificationForValidationFailure(ConstraintViolationException exception) {
        return createNotificationForValidationFailure(exception, AdditionalNotificationData.empty());
    }

    default Notification createNotificationForException(Throwable throwable, Object... exceptionMessageArgumentList) {
        return createNotificationForException(throwable, AdditionalNotificationData.empty(), exceptionMessageArgumentList);
    }

    default Notification createNotificationForAction(String actionName) {
        return createNotificationForAction(actionName, AdditionalNotificationData.empty());
    }
}
