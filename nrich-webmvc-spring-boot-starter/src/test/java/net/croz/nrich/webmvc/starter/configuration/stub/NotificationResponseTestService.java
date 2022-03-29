package net.croz.nrich.webmvc.starter.configuration.stub;

import net.croz.nrich.notification.api.model.AdditionalNotificationData;
import net.croz.nrich.notification.api.service.NotificationResolverService;
import net.croz.nrich.notification.api.service.BaseNotificationResponseService;
import org.springframework.validation.Errors;

import javax.validation.ConstraintViolationException;

public class NotificationResponseTestService implements BaseNotificationResponseService<Object> {

    @Override
    public Object responseWithValidationFailureNotification(Errors errors, Class<?> validationFailedOwningType, AdditionalNotificationData additionalNotificationData) {
        return null;
    }

    @Override
    public Object responseWithValidationFailureNotification(ConstraintViolationException exception, AdditionalNotificationData additionalNotificationData) {
        return null;
    }

    @Override
    public Object responseWithExceptionNotification(Throwable throwable, AdditionalNotificationData additionalNotificationData, Object... exceptionMessageArgumentList) {
        return null;
    }

    @Override
    public Object responseWithNotification(String actionName, AdditionalNotificationData additionalNotificationData) {
        return null;
    }

    @Override
    public Object responseWithNotificationActionResolvedFromRequest(AdditionalNotificationData additionalNotificationData) {
        return null;
    }

    @Override
    public NotificationResolverService notificationResolverService() {
        return null;
    }
}
