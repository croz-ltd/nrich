package net.croz.nrich.notification.service;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.notification.api.model.AdditionalNotificationData;
import net.croz.nrich.notification.api.model.Notification;
import net.croz.nrich.notification.api.model.NotificationSeverity;
import net.croz.nrich.notification.api.model.ValidationError;
import net.croz.nrich.notification.api.model.ValidationFailureNotification;
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
    public ValidationFailureNotification createNotificationForValidationFailure(final Errors errors, final Class<?> validationFailedOwningType, final AdditionalNotificationData additionalNotificationData) {
        final String typeName = validationFailedOwningType == null ? null : validationFailedOwningType.getName();
        final NotificationSeverity severity = Optional.ofNullable(additionalNotificationData.getSeverity()).orElse(NotificationSeverity.WARNING);

        final String title;
        if (typeName == null) {
            title = notificationMessageResolverService.resolveMessage(toList(NotificationConstants.VALIDATION_FAILED_MESSAGE_TITLE_CODE), NotificationConstants.VALIDATION_FAILED_DEFAULT_TITLE);
        }
        else {
            final String titleCode = String.format(NotificationConstants.PREFIX_MESSAGE_FORMAT, typeName, NotificationConstants.MESSAGE_TITLE_SUFFIX);

            title = notificationMessageResolverService.resolveMessage(toList(titleCode, NotificationConstants.VALIDATION_FAILED_MESSAGE_TITLE_CODE), NotificationConstants.VALIDATION_FAILED_DEFAULT_TITLE);
        }

        final String content = notificationMessageResolverService.resolveMessage(toList(NotificationConstants.VALIDATION_FAILED_CONTENT_CODE));
        final List<ValidationError> validationErrorList = convertValidationErrorsToMessageList(errors, validationFailedOwningType);
        final List<String> additionalNotificationDataMessageList = resolveMessageListFromNotificationData(additionalNotificationData.getMessageListDataMap());
        final List<String> validationMessageList = validationErrorList.stream().flatMap(value -> value.getErrorMessageList().stream()).collect(Collectors.toList());

        final List<String> messageList = Stream.concat(additionalNotificationDataMessageList.stream(), validationMessageList.stream()).collect(Collectors.toList());

        return new ValidationFailureNotification(title, content, messageList, severity, additionalNotificationData.getUxNotificationOptions(), validationErrorList);
    }

    @Override
    public ValidationFailureNotification createNotificationForValidationFailure(final ConstraintViolationException exception, final AdditionalNotificationData additionalNotificationData) {
        final Object target = constraintConversionService.resolveTarget(exception.getConstraintViolations());
        final Errors errors = constraintConversionService.convertConstraintViolationsToErrors(exception.getConstraintViolations(), target, NotificationConstants.UNKNOWN_VALIDATION_TARGET);

        final Class<?> targetClass = Optional.ofNullable(target).map(Object::getClass).orElse(null);

        return createNotificationForValidationFailure(errors, targetClass, additionalNotificationData);
    }

    @Override
    public Notification createNotificationForException(final Throwable throwable, final AdditionalNotificationData additionalNotificationData, Object... exceptionMessageArgumentList) {
        final String typeName = throwable.getClass().getName();
        final String titleCode = String.format(NotificationConstants.PREFIX_MESSAGE_FORMAT, typeName, NotificationConstants.MESSAGE_TITLE_SUFFIX);

        final String title = notificationMessageResolverService.resolveMessage(toList(titleCode, NotificationConstants.ERROR_OCCURRED_MESSAGE_TITLE_CODE), NotificationConstants.ERROR_OCCURRED_DEFAULT_TITLE);
        final String contentCode = String.format(NotificationConstants.PREFIX_MESSAGE_FORMAT, typeName, NotificationConstants.MESSAGE_CONTENT_SUFFIX);
        final String severityCode = String.format(NotificationConstants.PREFIX_MESSAGE_FORMAT, typeName, NotificationConstants.MESSAGE_SEVERITY_SUFFIX);

        final String content = notificationMessageResolverService.resolveMessage(toList(contentCode, NotificationConstants.ERROR_OCCURRED_DEFAULT_CODE), toList(exceptionMessageArgumentList), null);
        final NotificationSeverity severity = Optional.ofNullable(additionalNotificationData.getSeverity()).orElse(resolveExceptionSeverity(severityCode));

        return new Notification(title, content, resolveMessageListFromNotificationData(additionalNotificationData.getMessageListDataMap()), severity, additionalNotificationData.getUxNotificationOptions());
    }

    @Override
    public Notification createNotificationForAction(final String actionName, final AdditionalNotificationData additionalNotificationData) {
        final String titleCode = String.format(NotificationConstants.PREFIX_MESSAGE_FORMAT, actionName, NotificationConstants.MESSAGE_TITLE_SUFFIX);
        final String contentCode = String.format(NotificationConstants.PREFIX_MESSAGE_FORMAT, actionName, NotificationConstants.MESSAGE_CONTENT_SUFFIX);

        final String title = notificationMessageResolverService.resolveMessage(toList(titleCode, NotificationConstants.SUCCESS_MESSAGE_TITLE_CODE), NotificationConstants.SUCCESS_DEFAULT_TITLE);
        final String content = notificationMessageResolverService.resolveMessage(toList(contentCode, NotificationConstants.SUCCESS_DEFAULT_CODE));
        final NotificationSeverity severity = Optional.ofNullable(additionalNotificationData.getSeverity()).orElse(NotificationSeverity.INFO);

        return new Notification(title, content, resolveMessageListFromNotificationData(additionalNotificationData.getMessageListDataMap()), severity, additionalNotificationData.getUxNotificationOptions());
    }

    private List<ValidationError> convertValidationErrorsToMessageList(final Errors errors, final Class<?> validationFailedOwningType) {
        final Map<String, List<String>> resultMap = new LinkedHashMap<>();

        for (final ObjectError objectError : errors.getAllErrors()) {
            final String constraintFieldName = constraintFieldNameOrDefault(objectError);
            final String message = notificationMessageResolverService.resolveMessageForObjectError(validationFailedOwningType, objectError);

            if (!resultMap.containsKey(constraintFieldName)) {
                resultMap.put(constraintFieldName, new ArrayList<>());
            }

            resultMap.get(constraintFieldName).add(message);
        }

        return resultMap.entrySet().stream()
                .map(value -> new ValidationError(value.getKey(), value.getValue()))
                .collect(Collectors.toList());
    }

    private List<String> resolveMessageListFromNotificationData(final Map<String, ?> additionalNotificationData) {
        if (additionalNotificationData == null) {
            return Collections.emptyList();
        }

        return additionalNotificationData.entrySet().stream()
                .map(this::resolveMessageForAdditionalData)
                .filter(message -> !NotificationConstants.UNDEFINED_MESSAGE_VALUE.equals(message))
                .collect(Collectors.toList());
    }

    private String constraintFieldNameOrDefault(final ObjectError objectError) {
        return objectError instanceof FieldError ? ((FieldError) objectError).getField() : ValidationError.CONTAINING_OBJECT_NAME;
    }

    private String resolveMessageForAdditionalData(final Map.Entry<String, ?> additionalDataEntry) {
        final String messageCode = String.format(NotificationConstants.ADDITIONAL_EXCEPTION_DATA_MESSAGE_CODE_FORMAT, additionalDataEntry.getKey());

        return notificationMessageResolverService.resolveMessage(toList(messageCode), toList(additionalDataEntry.getValue()), NotificationConstants.UNDEFINED_MESSAGE_VALUE);
    }

    private NotificationSeverity resolveExceptionSeverity(final String messageCode) {
        final String severityValue = notificationMessageResolverService.resolveMessage(toList(messageCode), NotificationSeverity.ERROR.name());

        return NotificationSeverity.valueOf(severityValue);
    }

    @SafeVarargs
    private final <T> List<T> toList(final T... codeList) {
        if (codeList == null) {
            return null;
        }

        return Arrays.asList(codeList);
    }
}
