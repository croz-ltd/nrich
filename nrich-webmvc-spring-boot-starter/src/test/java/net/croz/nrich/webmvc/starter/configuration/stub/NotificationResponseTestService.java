package net.croz.nrich.webmvc.starter.configuration.stub;

import net.croz.nrich.notification.api.service.NotificationResolverService;
import net.croz.nrich.notification.api.service.NotificationResponseService;
import org.springframework.validation.Errors;

import javax.validation.ConstraintViolationException;
import java.util.Map;

public class NotificationResponseTestService implements NotificationResponseService<Object> {

    @Override
    public Object responseWithValidationFailureNotification(final Errors errors, final Class<?> validationFailedOwningType) {
        return null;
    }

    @Override
    public Object responseWithValidationFailureNotification(final ConstraintViolationException exception) {
        return null;
    }

    @Override
    public Object responseWithExceptionNotification(final Throwable throwable, final Map<String, ?> messageListData, final Object... additionalMessageArgumentList) {
        return null;
    }

    @Override
    public <D> Object responseWithSuccessNotificationActionResolvedFromRequest(final D data) {
        return null;
    }

    @Override
    public <D> Object responseWithSuccessNotification(final D data, final String actionName) {
        return null;
    }

    @Override
    public NotificationResolverService notificationResolverService() {
        return null;
    }
}
