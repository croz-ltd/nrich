package net.croz.nrich.notification.service;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.notification.api.service.NotificationMessageResolverService;
import net.croz.nrich.notification.constant.NotificationConstants;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MessageSourceNotificationMessageResolverService implements NotificationMessageResolverService {

    private final MessageSource messageSource;

    @Override
    public String resolveMessage(List<String> messageCodeList, List<Object> argumentList, String defaultMessage) {
        Assert.notEmpty(messageCodeList, "Code list cannot be empty!");

        Object[] arguments = argumentList == null ? new Object[0] : argumentList.toArray(new Object[0]);

        DefaultMessageSourceResolvable messageSourceResolvable = new DefaultMessageSourceResolvable(messageCodeList.toArray(new String[0]), arguments, defaultMessage);

        return messageSource.getMessage(messageSourceResolvable, LocaleContextHolder.getLocale());
    }

    public String resolveMessageForObjectError(Class<?> validationFailedOwningType, ObjectError objectError) {
        String constraintName = objectError.getCode();
        String fieldName = objectError instanceof FieldError ? ((FieldError) objectError).getField() : null;
        String name = validationFailedOwningType == null ? "" : StringUtils.uncapitalize(validationFailedOwningType.getName());
        String shortName = validationFailedOwningType == null ? "" : StringUtils.uncapitalize(validationFailedOwningType.getSimpleName());

        List<String> messageCodeList = new ArrayList<>();

        if (fieldName == null) {
            messageCodeList.add(String.format(NotificationConstants.CONSTRAINT_SHORT_MESSAGE_FORMAT, name, constraintName, NotificationConstants.INVALID_SUFFIX));
            messageCodeList.add(String.format(NotificationConstants.CONSTRAINT_SHORT_MESSAGE_FORMAT, shortName, constraintName, NotificationConstants.INVALID_SUFFIX));
        }
        else {
            messageCodeList.add(String.format(NotificationConstants.CONSTRAINT_FULL_MESSAGE_FORMAT, name, fieldName, constraintName, NotificationConstants.INVALID_SUFFIX));
            messageCodeList.add(String.format(NotificationConstants.CONSTRAINT_FULL_MESSAGE_FORMAT, shortName, fieldName, constraintName, NotificationConstants.INVALID_SUFFIX));
            messageCodeList.add(String.format(NotificationConstants.CONSTRAINT_SHORT_MESSAGE_FORMAT, fieldName, constraintName, NotificationConstants.INVALID_SUFFIX));
        }

        messageCodeList.add(String.format(NotificationConstants.PREFIX_MESSAGE_FORMAT, constraintName, NotificationConstants.INVALID_SUFFIX));

        String message = resolveMessage(messageCodeList, argumentsWithoutMessageCodeResolvable(objectError.getArguments()), objectError.getDefaultMessage());

        if (fieldName != null) {
            messageCodeList.clear();

            messageCodeList.add(String.format(NotificationConstants.CONSTRAINT_SHORT_MESSAGE_FORMAT, name, fieldName, NotificationConstants.FIELD_LABEL_SUFFIX));
            messageCodeList.add(String.format(NotificationConstants.CONSTRAINT_SHORT_MESSAGE_FORMAT, shortName, fieldName, NotificationConstants.FIELD_LABEL_SUFFIX));
            messageCodeList.add(String.format(NotificationConstants.CONSTRAINT_SHORT_MESSAGE_FORMAT, fieldName, constraintName, NotificationConstants.FIELD_LABEL_SUFFIX));

            String fieldNameMessage = resolveMessage(messageCodeList, null, fieldName);

            message = String.format(NotificationConstants.FIELD_ERROR_FORMAT, fieldNameMessage, message);
        }

        return message;
    }

    private List<Object> argumentsWithoutMessageCodeResolvable(Object[] arguments) {
        if (arguments == null || arguments.length == 0) {
            return Collections.emptyList();
        }

        Object[] filteredArguments = arguments;
        if ((arguments[0] instanceof DefaultMessageSourceResolvable)) {
            filteredArguments = Arrays.copyOfRange(arguments, 1, arguments.length);
        }

        return Arrays.stream(filteredArguments)
            .map(value -> value instanceof Object[] ? convertToString((Object[]) value) : value)
            .collect(Collectors.toList());
    }

    private String convertToString(Object[] value) {
        return Arrays.toString(value).replace('[', ' ').replace(']', ' ').trim();
    }
}
