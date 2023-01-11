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

package net.croz.nrich.notification.service;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.notification.api.model.AdditionalNotificationData;
import net.croz.nrich.notification.api.model.Notification;
import net.croz.nrich.notification.api.model.NotificationSeverity;
import net.croz.nrich.notification.api.model.ValidationError;
import net.croz.nrich.notification.api.model.ValidationFailureNotification;
import net.croz.nrich.notification.api.service.ConstraintConversionService;
import net.croz.nrich.notification.api.service.NotificationMessageResolverService;
import net.croz.nrich.notification.api.service.NotificationResolverService;
import net.croz.nrich.notification.constant.NotificationConstants;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class DefaultNotificationResolverService implements NotificationResolverService {

    private final NotificationMessageResolverService notificationMessageResolverService;

    private final ConstraintConversionService constraintConversionService;

    @Override
    public ValidationFailureNotification createNotificationForValidationFailure(Errors errors, Class<?> validationFailedOwningType, AdditionalNotificationData additionalNotificationData) {
        String typeName = validationFailedOwningType == null ? null : validationFailedOwningType.getName();
        NotificationSeverity severity = Optional.ofNullable(additionalNotificationData.getSeverity()).orElse(NotificationSeverity.WARNING);

        String title;
        if (typeName == null) {
            title = notificationMessageResolverService.resolveMessage(toList(NotificationConstants.VALIDATION_FAILED_MESSAGE_TITLE_CODE), NotificationConstants.EMPTY_MESSAGE);
        }
        else {
            String titleCode = String.format(NotificationConstants.PREFIX_MESSAGE_FORMAT, typeName, NotificationConstants.MESSAGE_TITLE_SUFFIX);

            title = notificationMessageResolverService.resolveMessage(toList(titleCode, NotificationConstants.VALIDATION_FAILED_MESSAGE_TITLE_CODE), NotificationConstants.EMPTY_MESSAGE);
        }

        String content = notificationMessageResolverService.resolveMessage(toList(NotificationConstants.VALIDATION_FAILED_CONTENT_CODE));
        List<ValidationError> validationErrorList = convertValidationErrorsToMessageList(errors, validationFailedOwningType);
        List<String> additionalNotificationDataMessageList = resolveMessageListFromNotificationData(additionalNotificationData.getMessageListDataMap());
        List<String> validationMessageList = validationErrorList.stream()
            .flatMap(value -> value.getErrorMessageList().stream())
            .collect(Collectors.toList());

        List<String> messageList = Stream.concat(additionalNotificationDataMessageList.stream(), validationMessageList.stream()).collect(Collectors.toList());

        return new ValidationFailureNotification(title, content, messageList, severity, additionalNotificationData.getUxNotificationOptions(), validationErrorList);
    }

    @Override
    public ValidationFailureNotification createNotificationForValidationFailure(ConstraintViolationException exception, AdditionalNotificationData additionalNotificationData) {
        Object target = constraintConversionService.resolveTarget(exception.getConstraintViolations());
        Errors errors = constraintConversionService.convertConstraintViolationsToErrors(exception.getConstraintViolations(), target, NotificationConstants.UNKNOWN_VALIDATION_TARGET);

        Class<?> targetClass = Optional.ofNullable(target).map(Object::getClass).orElse(null);

        return createNotificationForValidationFailure(errors, targetClass, additionalNotificationData);
    }

    @Override
    public Notification createNotificationForException(Throwable throwable, AdditionalNotificationData additionalNotificationData, Object... exceptionMessageArgumentList) {
        String typeName = throwable.getClass().getName();
        String titleCode = String.format(NotificationConstants.PREFIX_MESSAGE_FORMAT, typeName, NotificationConstants.MESSAGE_TITLE_SUFFIX);

        String title = notificationMessageResolverService.resolveMessage(toList(titleCode, NotificationConstants.ERROR_OCCURRED_MESSAGE_TITLE_CODE), NotificationConstants.EMPTY_MESSAGE);
        String contentCode = String.format(NotificationConstants.PREFIX_MESSAGE_FORMAT, typeName, NotificationConstants.MESSAGE_CONTENT_SUFFIX);
        String severityCode = String.format(NotificationConstants.PREFIX_MESSAGE_FORMAT, typeName, NotificationConstants.MESSAGE_SEVERITY_SUFFIX);

        String content = notificationMessageResolverService.resolveMessage(toList(contentCode, NotificationConstants.ERROR_OCCURRED_DEFAULT_CODE), toList(exceptionMessageArgumentList), null);
        NotificationSeverity severity = Optional.ofNullable(additionalNotificationData.getSeverity()).orElse(resolveExceptionSeverity(severityCode));
        List<String> messageList = resolveMessageListFromNotificationData(additionalNotificationData.getMessageListDataMap());

        return new Notification(title, content, messageList, severity, additionalNotificationData.getUxNotificationOptions());
    }

    @Override
    public Notification createNotificationForAction(String actionName, AdditionalNotificationData additionalNotificationData) {
        String titleCode = String.format(NotificationConstants.PREFIX_MESSAGE_FORMAT, actionName, NotificationConstants.MESSAGE_TITLE_SUFFIX);
        String contentCode = String.format(NotificationConstants.PREFIX_MESSAGE_FORMAT, actionName, NotificationConstants.MESSAGE_CONTENT_SUFFIX);

        String title = notificationMessageResolverService.resolveMessage(toList(titleCode, NotificationConstants.SUCCESS_MESSAGE_TITLE_CODE), NotificationConstants.EMPTY_MESSAGE);
        String content = notificationMessageResolverService.resolveMessage(toList(contentCode, NotificationConstants.SUCCESS_DEFAULT_CODE));
        NotificationSeverity severity = Optional.ofNullable(additionalNotificationData.getSeverity()).orElse(NotificationSeverity.INFO);
        List<String> messageList = resolveMessageListFromNotificationData(additionalNotificationData.getMessageListDataMap());

        return new Notification(title, content, messageList, severity, additionalNotificationData.getUxNotificationOptions());
    }

    private List<ValidationError> convertValidationErrorsToMessageList(Errors errors, Class<?> validationFailedOwningType) {
        Map<String, List<String>> resultMap = new LinkedHashMap<>();

        for (ObjectError objectError : errors.getAllErrors()) {
            String constraintFieldName = constraintFieldNameOrDefault(objectError);
            String message = notificationMessageResolverService.resolveMessageForObjectError(validationFailedOwningType, objectError);

            resultMap.computeIfAbsent(constraintFieldName, key -> new ArrayList<>());

            resultMap.get(constraintFieldName).add(message);
        }

        return resultMap.entrySet().stream()
            .map(value -> new ValidationError(value.getKey(), value.getValue()))
            .collect(Collectors.toList());
    }

    private List<String> resolveMessageListFromNotificationData(Map<String, ?> additionalNotificationData) {
        if (additionalNotificationData == null) {
            return Collections.emptyList();
        }

        return additionalNotificationData.entrySet().stream()
            .map(this::resolveMessageForAdditionalData)
            .filter(message -> !NotificationConstants.UNDEFINED_MESSAGE_VALUE.equals(message))
            .collect(Collectors.toList());
    }

    private String constraintFieldNameOrDefault(ObjectError objectError) {
        return objectError instanceof FieldError ? ((FieldError) objectError).getField() : ValidationError.CONTAINING_OBJECT_NAME;
    }

    private String resolveMessageForAdditionalData(Map.Entry<String, ?> additionalDataEntry) {
        String messageCode = String.format(NotificationConstants.ADDITIONAL_EXCEPTION_DATA_MESSAGE_CODE_FORMAT, additionalDataEntry.getKey());

        return notificationMessageResolverService.resolveMessage(toList(messageCode), toList(additionalDataEntry.getValue()), NotificationConstants.UNDEFINED_MESSAGE_VALUE);
    }

    private NotificationSeverity resolveExceptionSeverity(String messageCode) {
        String severityValue = notificationMessageResolverService.resolveMessage(toList(messageCode), NotificationSeverity.ERROR.name());

        return NotificationSeverity.valueOf(severityValue);
    }

    @SafeVarargs
    private final <T> List<T> toList(T... codeList) {
        if (codeList == null) {
            return Collections.emptyList();
        }

        return Arrays.asList(codeList);
    }
}
