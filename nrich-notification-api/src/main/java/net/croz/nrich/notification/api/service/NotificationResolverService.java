package net.croz.nrich.notification.api.service;

import net.croz.nrich.notification.api.model.Notification;
import net.croz.nrich.notification.api.model.ValidationFailureNotification;
import org.springframework.validation.Errors;

import javax.validation.ConstraintViolationException;
import java.util.Map;

public interface NotificationResolverService {

    ValidationFailureNotification createNotificationForValidationFailure(Errors errors, Class<?> validationFailedOwningType, Map<String, ?> messageListData);

    ValidationFailureNotification createNotificationForValidationFailure(Errors errors, Class<?> validationFailedOwningType);

    ValidationFailureNotification createNotificationForValidationFailure(ConstraintViolationException exception, Map<String, ?> messageListData);

    ValidationFailureNotification createNotificationForValidationFailure(ConstraintViolationException exception);

    Notification createNotificationForException(Throwable throwable, Map<String, ?> messageListData, Object... additionalMessageArgumentList);

    Notification createNotificationForException(Throwable throwable, Object... additionalMessageArgumentList);

    Notification createNotificationForSuccessfulAction(String actionName, Map<String, ?> messageListData);

    Notification createNotificationForSuccessfulAction(String actionName);
}
