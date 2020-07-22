package net.croz.nrich.webmvc.starter.configuration.stub;

import net.croz.nrich.notification.api.model.AdditionalNotificationData;
import net.croz.nrich.notification.api.service.NotificationResolverService;
import net.croz.nrich.notification.api.service.NotificationResponseService;
import org.springframework.validation.Errors;

import javax.validation.ConstraintViolationException;

public class NotificationResponseTestService implements NotificationResponseService<Object> {

    @Override
    public Object responseWithValidationFailureNotification(final Errors errors, final Class<?> validationFailedOwningType, final AdditionalNotificationData additionalNotificationData) {
        return null;
    }

    @Override
    public Object responseWithValidationFailureNotification(final ConstraintViolationException exception, final AdditionalNotificationData additionalNotificationData) {
        return null;
    }

    @Override
    public Object responseWithExceptionNotification(final Throwable throwable, final AdditionalNotificationData additionalNotificationData, final Object... exceptionMessageArgumentList) {
        return null;
    }

    @Override
    public <D> Object responseWithNotificationActionResolvedFromRequest(final D data, final AdditionalNotificationData additionalNotificationData) {
        return null;
    }

    @Override
    public <D> Object responseWithNotification(final D data, final String actionName, final AdditionalNotificationData additionalNotificationData) {
        return null;
    }

    @Override
    public NotificationResolverService notificationResolverService() {
        return null;
    }
}
