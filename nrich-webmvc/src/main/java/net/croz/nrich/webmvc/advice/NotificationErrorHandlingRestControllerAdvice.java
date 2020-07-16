package net.croz.nrich.webmvc.advice;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.core.api.exception.ExceptionWithArguments;
import net.croz.nrich.logging.api.service.LoggingService;
import net.croz.nrich.notification.api.model.AdditionalNotificationData;
import net.croz.nrich.notification.api.service.NotificationResponseService;
import net.croz.nrich.webmvc.api.service.ExceptionAuxiliaryDataResolverService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// TODO enable resolving of http status for each exception
@RestControllerAdvice
@RequiredArgsConstructor
public class NotificationErrorHandlingRestControllerAdvice {

    private final List<String> exceptionToUnwrapList;

    private final List<String> exceptionAuxiliaryDataToIncludeInNotification;

    private final NotificationResponseService<?> notificationResponseService;

    private final LoggingService loggingService;

    private final ExceptionAuxiliaryDataResolverService exceptionAuxiliaryDataResolverService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception, final HttpServletRequest request) {
        logExceptionWithResolvedAuxiliaryData(exception, request);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(notificationResponseService.responseWithValidationFailureNotification(exception.getBindingResult(), exception.getParameter().getParameterType()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(final ConstraintViolationException exception, final HttpServletRequest request) {
        logExceptionWithResolvedAuxiliaryData(exception, request);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(notificationResponseService.responseWithValidationFailureNotification(exception));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> handleBindException(final BindException exception, final HttpServletRequest request) {
        logExceptionWithResolvedAuxiliaryData(exception, request);

        final Class<?> targetClass = Optional.ofNullable(exception.getTarget()).map(Object::getClass).orElse(null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(notificationResponseService.responseWithValidationFailureNotification(exception.getBindingResult(), targetClass));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(final Exception exception, final HttpServletRequest request) {
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

        final Map<String, Object> exceptionAuxiliaryData = exceptionAuxiliaryData(exception, request);

        loggingService.logInternalException(unwrappedException, exceptionAuxiliaryData);

        Map<String, ?> notificationAuxiliaryData = null;
        if (exceptionAuxiliaryData != null && exceptionAuxiliaryDataToIncludeInNotification != null) {
            notificationAuxiliaryData = exceptionAuxiliaryData.entrySet().stream()
                    .filter(value -> exceptionAuxiliaryDataToIncludeInNotification.contains(value.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }


        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(notificationResponseService.responseWithExceptionNotification(unwrappedException, AdditionalNotificationData.builder().messageListDataMap(notificationAuxiliaryData).build(), exceptionArgumentList(exception)));
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
        final Map<String, ?> exceptionAuxiliaryData = exceptionAuxiliaryData(exception, request);

        loggingService.logInternalException(exception, exceptionAuxiliaryData);
    }

    private Map<String, Object> exceptionAuxiliaryData(final Exception exception, final HttpServletRequest request) {
        return Optional.ofNullable(exceptionAuxiliaryDataResolverService).map(service -> service.resolveRequestExceptionAuxiliaryData(exception, request)).orElse(Collections.emptyMap());
    }
}
