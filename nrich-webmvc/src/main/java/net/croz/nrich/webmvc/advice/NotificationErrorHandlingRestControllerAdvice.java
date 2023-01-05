/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package net.croz.nrich.webmvc.advice;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.core.api.exception.ExceptionWithArguments;
import net.croz.nrich.logging.api.service.LoggingService;
import net.croz.nrich.notification.api.model.AdditionalNotificationData;
import net.croz.nrich.notification.api.service.BaseNotificationResponseService;
import net.croz.nrich.webmvc.api.service.ExceptionAuxiliaryDataResolverService;
import net.croz.nrich.webmvc.api.service.ExceptionHttpStatusResolverService;
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

@RestControllerAdvice
@RequiredArgsConstructor
public class NotificationErrorHandlingRestControllerAdvice {

    private final List<String> exceptionToUnwrapList;

    private final List<String> exceptionAuxiliaryDataToIncludeInNotification;

    private final BaseNotificationResponseService<?> notificationResponseService;

    private final LoggingService loggingService;

    private final ExceptionAuxiliaryDataResolverService exceptionAuxiliaryDataResolverService;

    private final ExceptionHttpStatusResolverService httpStatusResolverService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletRequest request) {
        logExceptionWithResolvedAuxiliaryData(exception, request);

        HttpStatus status = resolveHttpStatusForException(exception, HttpStatus.BAD_REQUEST);

        return ResponseEntity.status(status).body(notificationResponseService.responseWithValidationFailureNotification(exception.getBindingResult(), exception.getParameter().getParameterType()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException exception, HttpServletRequest request) {
        logExceptionWithResolvedAuxiliaryData(exception, request);

        HttpStatus status = resolveHttpStatusForException(exception, HttpStatus.BAD_REQUEST);

        return ResponseEntity.status(status).body(notificationResponseService.responseWithValidationFailureNotification(exception));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Object> handleBindException(BindException exception, HttpServletRequest request) {
        logExceptionWithResolvedAuxiliaryData(exception, request);

        Class<?> targetClass = Optional.ofNullable(exception.getTarget()).map(Object::getClass).orElse(null);

        HttpStatus status = resolveHttpStatusForException(exception, HttpStatus.BAD_REQUEST);

        return ResponseEntity.status(status).body(notificationResponseService.responseWithValidationFailureNotification(exception.getBindingResult(), targetClass));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception exception, HttpServletRequest request) {
        Exception unwrappedException = unwrapException(exception);

        if (unwrappedException instanceof MethodArgumentNotValidException) {
            return handleMethodArgumentNotValidException((MethodArgumentNotValidException) unwrappedException, request);
        }
        else if (unwrappedException instanceof BindException) {
            return handleBindException((BindException) unwrappedException, request);
        }
        else if (unwrappedException instanceof ConstraintViolationException) {
            return handleConstraintViolationException((ConstraintViolationException) unwrappedException, request);
        }

        Map<String, Object> exceptionAuxiliaryData = resolveExceptionAuxiliaryData(exception, request);

        loggingService.logInternalException(unwrappedException, exceptionAuxiliaryData);

        Map<String, ?> notificationAuxiliaryData = null;
        if (exceptionAuxiliaryData != null && exceptionAuxiliaryDataToIncludeInNotification != null) {
            notificationAuxiliaryData = exceptionAuxiliaryData.entrySet().stream()
                .filter(value -> exceptionAuxiliaryDataToIncludeInNotification.contains(value.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        HttpStatus status = resolveHttpStatusForException(unwrappedException, HttpStatus.INTERNAL_SERVER_ERROR);
        AdditionalNotificationData additionalNotificationData = AdditionalNotificationData.builder().messageListDataMap(notificationAuxiliaryData).build();
        Object[] argumentList = resolveExceptionArgumentList(unwrappedException);

        return ResponseEntity.status(status).body(notificationResponseService.responseWithExceptionNotification(unwrappedException, additionalNotificationData, argumentList));
    }

    private Exception unwrapException(Exception exception) {
        if (exceptionToUnwrapList != null && exceptionToUnwrapList.contains(exception.getClass().getName()) && exception.getCause() != null) {
            return (Exception) exception.getCause();
        }

        return exception;
    }

    private void logExceptionWithResolvedAuxiliaryData(Exception exception, HttpServletRequest request) {
        Map<String, Object> exceptionAuxiliaryData = resolveExceptionAuxiliaryData(exception, request);

        loggingService.logInternalException(exception, exceptionAuxiliaryData);
    }

    private Object[] resolveExceptionArgumentList(Exception exception) {
        return exception instanceof ExceptionWithArguments ? ((ExceptionWithArguments) exception).getArgumentList() : null;
    }

    private Map<String, Object> resolveExceptionAuxiliaryData(Exception exception, HttpServletRequest request) {
        return Optional.ofNullable(exceptionAuxiliaryDataResolverService).map(service -> service.resolveRequestExceptionAuxiliaryData(exception, request)).orElse(Collections.emptyMap());
    }

    private HttpStatus resolveHttpStatusForException(Exception exception, HttpStatus defaultStatus) {
        return Optional.ofNullable(httpStatusResolverService.resolveHttpStatusForException(exception)).map(HttpStatus::resolve).orElse(defaultStatus);
    }
}
