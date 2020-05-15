package net.croz.nrich.notification.service;

import net.croz.nrich.notification.model.Notification;
import net.croz.nrich.notification.model.ValidationFailureNotification;
import org.springframework.validation.Errors;

import java.util.Map;

public interface NotificationResolverService {

    ValidationFailureNotification createMessageNotificationForValidationFailure(Errors errors, Class<?> validationFailedOwningType, Map<String, ?> messageListData);

    ValidationFailureNotification createMessageNotificationForValidationFailure(Errors errors, Class<?> validationFailedOwningType);

    Notification createNotificationForException(Throwable throwable, Map<String, ?> messageListData, Object... additionalMessageArgumentList);

    Notification createNotificationForException(Throwable throwable, Object... additionalMessageArgumentList);

    Notification createNotificationForSuccessfulAction(String actionName, Map<String, ?> messageListData);

    Notification createNotificationForSuccessfulAction(String actionName);
}
