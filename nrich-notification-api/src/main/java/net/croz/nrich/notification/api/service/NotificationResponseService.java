package net.croz.nrich.notification.api.service;

import org.springframework.validation.Errors;

import javax.validation.ConstraintViolationException;
import java.util.Map;

// TODO check if additional method are needed
public interface NotificationResponseService<T> {

    T responseWithValidationFailureNotification(Errors errors, Class<?> validationFailedOwningType);

    T responseWithValidationFailureNotification(ConstraintViolationException exception);

    T responseWithExceptionNotification(Throwable throwable, Map<String, ?> messageListData, Object... additionalMessageArgumentList);

    <D> T responseWithSuccessNotificationActionResolvedFromRequest(D data);

    <D> T responseWithSuccessNotification(D data, String actionName);
}
