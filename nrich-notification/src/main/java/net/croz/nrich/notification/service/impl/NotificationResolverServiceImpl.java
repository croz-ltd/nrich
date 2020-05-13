package net.croz.nrich.notification.service.impl;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.notification.constant.NotificationResolverConstants;
import net.croz.nrich.notification.model.Notification;
import net.croz.nrich.notification.model.NotificationSeverity;
import net.croz.nrich.notification.model.ValidationError;
import net.croz.nrich.notification.model.ValidationFailureNotification;
import net.croz.nrich.notification.service.NotificationResolverService;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class NotificationResolverServiceImpl implements NotificationResolverService {

    private final MessageSource messageSource;

    @Override
    public ValidationFailureNotification createMessageNotificationForValidationFailure(final Errors errors, final Class<?> validationFailedOwningType) {
        return createMessageNotificationForValidationFailure(errors, validationFailedOwningType, null);
    }

    @Override
    public ValidationFailureNotification createMessageNotificationForValidationFailure(final Errors errors, final Class<?> validationFailedOwningType, final Map<String, ?> additionalNotificationData) {
        final String typeName = validationFailedOwningType == null ? null : validationFailedOwningType.getName();

        final String title;
        if (typeName == null) {
            title = getMessage(NotificationResolverConstants.VALIDATION_FAILED_MESSAGE_TITLE_CODE, NotificationResolverConstants.VALIDATION_FAILED_DEFAULT_TITLE);
        }
        else {
            final String titleCode = String.format(NotificationResolverConstants.PREFIX_MESSAGE_FORMAT, typeName, NotificationResolverConstants.MESSAGE_TITLE_SUFFIX);
            title = resolveMessageOrDefault(titleCode, NotificationResolverConstants.VALIDATION_FAILED_MESSAGE_TITLE_CODE, NotificationResolverConstants.VALIDATION_FAILED_DEFAULT_TITLE);
        }

        final String contentText = getMessage(NotificationResolverConstants.VALIDATION_FAILED_CONTENT_CODE, null);
        final List<ValidationError> validationErrorList = convertValidationErrorsToMessageList(errors, validationFailedOwningType);
        final List<String> additionalNotificationDataMessageList = resolveMessageListFromNotificationData(additionalNotificationData);
        final List<String> validationMessageList = validationErrorList.stream().flatMap(value -> value.getErrorMessageList().stream()).collect(Collectors.toList());

        final List<String> messageList = Stream.concat(additionalNotificationDataMessageList.stream(), validationMessageList.stream()).collect(Collectors.toList());

        return new ValidationFailureNotification(title, contentText, messageList, NotificationSeverity.WARN, validationErrorList);
    }

    @Override
    public Notification createNotificationForException(final Throwable throwable, final Object... additionalMessageArgumentList) {
        return createNotificationForException(throwable, null, additionalMessageArgumentList);
    }

    @Override
    public Notification createNotificationForException(final Throwable throwable, final Map<String, ?> additionalNotificationData, final Object... additionalMessageArgumentList) {
        final String typeName = throwable.getClass().getName();
        final String titleCode = String.format(NotificationResolverConstants.PREFIX_MESSAGE_FORMAT, typeName, NotificationResolverConstants.MESSAGE_TITLE_SUFFIX);

        final String title = resolveMessageOrDefault(titleCode, NotificationResolverConstants.ERROR_OCCURRED_MESSAGE_TITLE_CODE, NotificationResolverConstants.ERROR_OCCURRED_DEFAULT_TITLE);
        final String messageCode = String.format(NotificationResolverConstants.PREFIX_MESSAGE_FORMAT, typeName, NotificationResolverConstants.MESSAGE_SUFFIX);
        final String severityCode = String.format(NotificationResolverConstants.PREFIX_MESSAGE_FORMAT, typeName, NotificationResolverConstants.MESSAGE_SEVERITY_SUFFIX);

        final String message = resolveMessageOrDefault(messageCode, NotificationResolverConstants.ERROR_OCCURRED_DEFAULT_CODE, null, additionalMessageArgumentList);
        final NotificationSeverity severity = resolveExceptionSeverity(severityCode);

        return new Notification(title, message, resolveMessageListFromNotificationData(additionalNotificationData), severity);
    }

    @Override
    public Notification createNotificationForSuccessfulAction(final String actionName) {
        return createNotificationForSuccessfulAction(actionName, null);
    }

    @Override
    public Notification createNotificationForSuccessfulAction(final String actionName, final Map<String, ?> additionalNotificationData) {
        final String titleCode = String.format(NotificationResolverConstants.PREFIX_MESSAGE_FORMAT, actionName, NotificationResolverConstants.MESSAGE_TITLE_SUFFIX);
        final String messageCode = String.format(NotificationResolverConstants.PREFIX_MESSAGE_FORMAT, actionName, NotificationResolverConstants.MESSAGE_SUFFIX);

        final String title = resolveMessageOrDefault(titleCode, NotificationResolverConstants.SUCCESS_MESSAGE_TITLE_CODE, NotificationResolverConstants.SUCCESS_DEFAULT_TITLE);
        final String message = resolveMessageOrDefault(messageCode, NotificationResolverConstants.SUCCESS_DEFAULT_CODE, null);

        return new Notification(title, message, resolveMessageListFromNotificationData(additionalNotificationData), NotificationSeverity.INFO);
    }

    private List<ValidationError> convertValidationErrorsToMessageList(final Errors errors, final Class<?> validationFailedOwningType) {
        final String prefix = validationFailedOwningType == null ? null : validationFailedOwningType.getName();

        final Map<String, List<String>> resultMap = new LinkedHashMap<>();

        for (final ObjectError objectError : errors.getAllErrors()) {
            final String constraintFieldName = constraintFieldNameOrDefault(objectError, ValidationError.CONTAINING_OBJECT_NAME);
            final List<String> messageCodeList = resolveMessageCodeListForObjectError(prefix, objectError);
            final String message = resolveMessage(messageCodeList, objectError.getArguments(), objectError.getDefaultMessage());

            if (!resultMap.containsKey(constraintFieldName)) {
                resultMap.put(constraintFieldName, new ArrayList<>());
            }

            resultMap.get(constraintFieldName).add(message);
        }

        return resultMap.entrySet().stream()
                .map(value -> new ValidationError(value.getKey(), value.getValue()))
                .collect(Collectors.toList());
    }

    private String resolveMessage(final List<String> messageCodeList, final Object[] argumentList, final String defaultMessage) {
        final DefaultMessageSourceResolvable messageCodeResolvable = new DefaultMessageSourceResolvable(messageCodeList.toArray(new String[0]), argumentsWithoutMessageCodeResolvable(argumentList), defaultMessage);

        return messageSource.getMessage(messageCodeResolvable, LocaleContextHolder.getLocale());
    }

    private List<String> resolveMessageCodeListForObjectError(final String prefix, final ObjectError objectError) {
        final String[] defaultCodeList = objectError.getCodes() == null ? new String[0] : objectError.getCodes();
        final String fieldName = constraintFieldNameOrDefault(objectError, null);
        final String prefixWithFieldName = fieldName == null ? prefix : String.format(NotificationResolverConstants.PREFIX_MESSAGE_FORMAT, prefix, fieldName);

        final List<String> messageCodeList = new ArrayList<>(Arrays.asList(defaultCodeList));

        // reverse codes so most frequently used start first
        Collections.reverse(messageCodeList);

        final List<String> messageCodeWithPrefixList = messageCodeList.stream().map(value -> String.format(NotificationResolverConstants.PREFIX_MESSAGE_FORMAT, prefixWithFieldName, value)).collect(Collectors.toList());

        messageCodeList.addAll(0, messageCodeWithPrefixList);

        // add last message code that is usually constraint name
        if (fieldName != null && defaultCodeList.length > 0) {
            messageCodeList.add(messageCodeWithPrefixList.size(), String.format(NotificationResolverConstants.PREFIX_MESSAGE_FORMAT, fieldName, defaultCodeList[defaultCodeList.length - 1]));
        }

        return messageCodeList;
    }

    private Object[] argumentsWithoutMessageCodeResolvable(final Object[] arguments) {
        if (arguments == null || !(arguments[0] instanceof DefaultMessageSourceResolvable)) {
            return arguments;
        }

        return Arrays.copyOfRange(arguments, 1, arguments.length);
    }

    private String constraintFieldNameOrDefault(final ObjectError objectError, final String defaultValue) {
        return objectError instanceof FieldError ? ((FieldError) objectError).getField() : defaultValue;
    }

    private NotificationSeverity resolveExceptionSeverity(final String messageCode) {
        return NotificationSeverity.valueOf(getMessage(messageCode, NotificationSeverity.ERROR.name()));
    }

    private String resolveMessageOrDefault(final String messageCode, final String defaultMessageCode, final String defaultMessage, final Object... arguments) {
        try {
            return getMessage(messageCode, null, arguments);
        }
        catch (final NoSuchMessageException ignore) {
            return getMessage(defaultMessageCode, defaultMessage, arguments);
        }
    }

    private String getMessage(final String messageCode, final String defaultMessage, final Object... arguments) {
        final DefaultMessageSourceResolvable messageCodeResolvable = new DefaultMessageSourceResolvable(new String[] { messageCode }, arguments, defaultMessage);

        return messageSource.getMessage(messageCodeResolvable, LocaleContextHolder.getLocale());
    }

    private List<String> resolveMessageListFromNotificationData(final Map<String, ?> additionalNotificationData) {
        if (additionalNotificationData == null) {
            return Collections.emptyList();
        }

        return additionalNotificationData.entrySet().stream()
                .map(this::resolveMessageForAdditionalData)
                .filter(message -> !NotificationResolverConstants.UNDEFINED_MESSAGE_VALUE.equals(message))
                .collect(Collectors.toList());
    }

    private String resolveMessageForAdditionalData(final Map.Entry<String, ?> additionalDataEntry) {
        final String messageCode = String.format(NotificationResolverConstants.ADDITIONAL_EXCEPTION_DATA_MESSAGE_CODE_FORMAT, additionalDataEntry.getKey());

        return getMessage(messageCode, NotificationResolverConstants.UNDEFINED_MESSAGE_VALUE, additionalDataEntry.getValue());
    }
}
