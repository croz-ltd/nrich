package net.croz.nrich.notification.service;

import lombok.RequiredArgsConstructor;
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
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class WebMvcNotificationResponseService implements NotificationResponseService<ResponseWithNotification<?>> {

    private final NotificationResolverService notificationResolverService;

    @Override
    public ResponseWithNotification<?> responseWithValidationFailureNotification(final Errors errors, final Class<?> validationFailedOwningType) {
        final Notification notification = notificationResolverService.createNotificationForValidationFailure(errors, validationFailedOwningType);

        return new ResponseWithNotification<>(null, notification);
    }

    @Override
    public ResponseWithNotification<?> responseWithValidationFailureNotification(final ConstraintViolationException exception) {
        final Notification notification = notificationResolverService.createNotificationForValidationFailure(exception);

        return new ResponseWithNotification<>(null, notification);
    }

    @Override
    public ResponseWithNotification<?> responseWithExceptionNotification(final Throwable throwable, final Map<String, ?> messageListData, final Object... additionalMessageArgumentList) {
        final Notification notification = notificationResolverService.createNotificationForException(throwable, messageListData, additionalMessageArgumentList);

        return new ResponseWithNotification<>(null, notification);
    }

    @Override
    public <D> ResponseWithNotification<D> responseWithSuccessNotificationActionResolvedFromRequest(final D data) {
        final String actionName = extractActionNameFromCurrentRequest();

        return responseWithSuccessNotification(data, actionName);
    }

    @Override
    public <D> ResponseWithNotification<D> responseWithSuccessNotification(final D data, final String actionName) {
        final Notification notification = notificationResolverService.createNotificationForSuccessfulAction(actionName);

        return new ResponseWithNotification<>(data, notification);
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
