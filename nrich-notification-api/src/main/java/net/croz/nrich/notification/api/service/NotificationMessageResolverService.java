package net.croz.nrich.notification.api.service;

import org.springframework.validation.ObjectError;

import java.util.List;

public interface NotificationMessageResolverService {

    String resolveMessage(List<String> messageCodeList, List<Object> argumentList, String defaultMessage);

    String resolveMessageForObjectError(Class<?> validationFailedOwningType, ObjectError objectError);

    default String resolveMessage(List<String> messageCodeList, String defaultMessage) {
        return resolveMessage(messageCodeList, null, defaultMessage);
    }

    default String resolveMessage(List<String> messageCodeList) {
        return resolveMessage(messageCodeList, null, null);
    }
}
