package net.croz.nrich.notification.api.service;

import org.springframework.validation.ObjectError;

import java.util.List;

/**
 * Resolves messages for notification from either message code list or from Springs {@link ObjectError} instances.
 */
public interface NotificationMessageResolverService {

    /**
     * Returns message resolved from messageCodeList, ordering is important since first found message for message code will be returned.
     * If no message is found for any code default message is returned.
     *
     * @param messageCodeList message code list for which to resolve message for
     * @param argumentList arguments for message resolving
     * @param defaultMessage if no message has been found for message code list this message is returned
     * @return resolved message or default if none has been found
     */
    String resolveMessage(List<String> messageCodeList, List<Object> argumentList, String defaultMessage);

    /**
     * Resolves message for Springs {@link ObjectError}.
     *
     * @param validationFailedOwningType class on which {@link ObjectError} has been found
     * @param objectError validation failure error
     * @return resolved message
     */
    String resolveMessageForObjectError(Class<?> validationFailedOwningType, ObjectError objectError);

    default String resolveMessage(List<String> messageCodeList, String defaultMessage) {
        return resolveMessage(messageCodeList, null, defaultMessage);
    }

    default String resolveMessage(List<String> messageCodeList) {
        return resolveMessage(messageCodeList, null, null);
    }
}
