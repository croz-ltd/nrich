package net.croz.nrich.notification.service;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.notification.api.model.AdditionalNotificationData;
import net.croz.nrich.notification.api.model.Notification;
import net.croz.nrich.notification.api.response.ResponseWithNotification;
import net.croz.nrich.notification.api.service.NotificationResolverService;
import net.croz.nrich.notification.api.service.NotificationResponseService;
import org.springframework.validation.Errors;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.Objects;

@RequiredArgsConstructor
public class WebMvcNotificationResponseService implements NotificationResponseService<ResponseWithNotification<?>> {

    private final NotificationResolverService notificationResolverService;

    @Override
    public ResponseWithNotification<?> responseWithValidationFailureNotification(final Errors errors, final Class<?> validationFailedOwningType, final AdditionalNotificationData additionalNotificationData) {
        final Notification notification = notificationResolverService.createNotificationForValidationFailure(errors, validationFailedOwningType, additionalNotificationData);

        return new ResponseWithNotification<>(null, notification);
    }

    @Override
    public ResponseWithNotification<?> responseWithValidationFailureNotification(final ConstraintViolationException exception, final AdditionalNotificationData additionalNotificationData) {
        final Notification notification = notificationResolverService.createNotificationForValidationFailure(exception, additionalNotificationData);

        return new ResponseWithNotification<>(null, notification);
    }

    @Override
    public ResponseWithNotification<?> responseWithExceptionNotification(final Throwable throwable, final AdditionalNotificationData additionalNotificationData, final Object... exceptionMessageArgumentList) {
        final Notification notification = notificationResolverService.createNotificationForException(throwable, additionalNotificationData, exceptionMessageArgumentList);

        return new ResponseWithNotification<>(null, notification);
    }

    @Override
    public <D> ResponseWithNotification<D> responseWithNotificationActionResolvedFromRequest(final D data, final AdditionalNotificationData additionalNotificationData) {
        final String actionName = extractActionNameFromCurrentRequest();

        return responseWithNotification(data, actionName, additionalNotificationData);
    }

    @Override
    public <D> ResponseWithNotification<D> responseWithNotification(final D data, final String actionName, final AdditionalNotificationData additionalNotificationData) {
        final Notification notification = notificationResolverService.createNotificationForAction(actionName, additionalNotificationData);

        return new ResponseWithNotification<>(data, notification);
    }

    @Override
    public NotificationResolverService notificationResolverService() {
        return notificationResolverService;
    }

    private String extractActionNameFromCurrentRequest() {
        final HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        return extractActionNameFromRequest(request);
    }

    private String extractActionNameFromRequest(final HttpServletRequest request) {
        final String path = new UrlPathHelper().getPathWithinApplication(request);

        return path.substring(1).replace("/", ".");
    }
}
