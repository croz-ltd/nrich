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
    public ResponseWithNotification<?> responseWithValidationFailureNotification(Errors errors, Class<?> validationFailedOwningType, AdditionalNotificationData additionalNotificationData) {
        Notification notification = notificationResolverService.createNotificationForValidationFailure(errors, validationFailedOwningType, additionalNotificationData);

        return new ResponseWithNotification<>(null, notification);
    }

    @Override
    public ResponseWithNotification<?> responseWithValidationFailureNotification(ConstraintViolationException exception, AdditionalNotificationData additionalNotificationData) {
        Notification notification = notificationResolverService.createNotificationForValidationFailure(exception, additionalNotificationData);

        return new ResponseWithNotification<>(null, notification);
    }

    @Override
    public ResponseWithNotification<?> responseWithExceptionNotification(Throwable throwable, AdditionalNotificationData additionalNotificationData, Object... exceptionMessageArgumentList) {
        Notification notification = notificationResolverService.createNotificationForException(throwable, additionalNotificationData, exceptionMessageArgumentList);

        return new ResponseWithNotification<>(null, notification);
    }

    @Override
    public <D> ResponseWithNotification<D> responseWithNotificationActionResolvedFromRequest(D data, AdditionalNotificationData additionalNotificationData) {
        String actionName = extractActionNameFromCurrentRequest();

        return responseWithNotification(data, actionName, additionalNotificationData);
    }

    @Override
    public <D> ResponseWithNotification<D> responseWithNotification(D data, String actionName, AdditionalNotificationData additionalNotificationData) {
        Notification notification = notificationResolverService.createNotificationForAction(actionName, additionalNotificationData);

        return new ResponseWithNotification<>(data, notification);
    }

    @Override
    public NotificationResolverService notificationResolverService() {
        return notificationResolverService;
    }

    private String extractActionNameFromCurrentRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        return extractActionNameFromRequest(request);
    }

    private String extractActionNameFromRequest(HttpServletRequest request) {
        String path = new UrlPathHelper().getPathWithinApplication(request);

        return path.substring(1).replace("/", ".");
    }
}
