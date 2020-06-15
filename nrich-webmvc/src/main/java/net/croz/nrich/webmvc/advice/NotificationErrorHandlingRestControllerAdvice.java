package net.croz.nrich.webmvc.advice;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.core.api.exception.ExceptionWithArguments;
import net.croz.nrich.logging.api.service.LoggingService;
import net.croz.nrich.notification.api.model.Notification;
import net.croz.nrich.notification.api.service.NotificationResolverService;
import net.croz.nrich.webmvc.service.ExceptionAuxiliaryDataResolverService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// TODO enable resolving of http status for each exception
@RestControllerAdvice
@RequiredArgsConstructor
public class NotificationErrorHandlingRestControllerAdvice {

    public static final String NOTIFICATION_KEY = "notification";

    private final List<String> exceptionToUnwrapList;

    private final List<String> exceptionAuxiliaryDataToIncludeInNotification;

    private final NotificationResolverService notificationResolverService;

    private final LoggingService loggingService;

    private final ExceptionAuxiliaryDataResolverService exceptionAuxiliaryDataResolverService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Notification>> handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception, final HttpServletRequest request) {
        logExceptionWithResolvedAuxiliaryData(exception, request);

        final Notification notification = notificationResolverService.createMessageNotificationForValidationFailure(exception.getBindingResult(), exception.getParameter().getParameterType());

        return notificationResponse(notification, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Notification>> handleConstraintViolationException(final ConstraintViolationException exception, final HttpServletRequest request) {
        logExceptionWithResolvedAuxiliaryData(exception, request);

        final Notification notification = notificationResolverService.createMessageNotificationForValidationFailure(exception);

        return notificationResponse(notification, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, Notification>> handleBindException(final BindException exception, final HttpServletRequest request) {
        logExceptionWithResolvedAuxiliaryData(exception, request);

        final Class<?> targetClass = Optional.ofNullable(exception.getTarget()).map(Object::getClass).orElse(null);

        final Notification notification = notificationResolverService.createMessageNotificationForValidationFailure(exception.getBindingResult(), targetClass);

        return notificationResponse(notification, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Notification>> handleException(final Exception exception, final HttpServletRequest request) {
        final Exception unwrappedException = unwrapException(exception);

        if (unwrappedException instanceof MethodArgumentNotValidException) {
            return handleMethodArgumentNotValidException((MethodArgumentNotValidException) unwrappedException, request);
        }
        else if (unwrappedException instanceof BindException) {
            return handleBindException((BindException) unwrappedException, request);
        }
        else if (unwrappedException instanceof ConstraintViolationException) {
            return handleConstraintViolationException((ConstraintViolationException) unwrappedException, request);
        }

        final Map<String, Object> exceptionAuxiliaryData = exceptionAuxiliaryDataResolverService.resolveRequestExceptionAuxiliaryData(exception, request);

        loggingService.logInternalException(unwrappedException, exceptionAuxiliaryData);

        Map<String, ?> notificationAuxiliaryData = null;
        if (exceptionAuxiliaryData != null && exceptionAuxiliaryDataToIncludeInNotification != null) {
            notificationAuxiliaryData = exceptionAuxiliaryData.entrySet().stream()
                    .filter(value -> exceptionAuxiliaryDataToIncludeInNotification.contains(value.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        final Notification notification = notificationResolverService.createNotificationForException(unwrappedException, notificationAuxiliaryData, exceptionArgumentList(exception));

        return notificationResponse(notification, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Map<String, Notification>> notificationResponse(final Notification notification, final HttpStatus status) {
        final Map<String, Notification> notificationMap = new HashMap<>();

        notificationMap.put(NOTIFICATION_KEY, notification);

        return ResponseEntity.status(status).body(notificationMap);
    }

    private Exception unwrapException(final Exception exception) {
        if (exceptionToUnwrapList != null && exceptionToUnwrapList.contains(exception.getClass().getName()) && exception.getCause() != null) {
            return (Exception) exception.getCause();
        }

        return exception;
    }

    private Object[] exceptionArgumentList(final Exception exception) {
        return exception instanceof ExceptionWithArguments ? ((ExceptionWithArguments) exception).getArgumentList() : null;
    }

    private void logExceptionWithResolvedAuxiliaryData(final Exception exception, final HttpServletRequest request) {
        final Map<String, ?> exceptionAuxiliaryData = exceptionAuxiliaryDataResolverService.resolveRequestExceptionAuxiliaryData(exception, request);

        loggingService.logInternalException(exception, exceptionAuxiliaryData);
    }
}
