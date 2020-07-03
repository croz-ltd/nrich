package net.croz.nrich.notification.service;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.notification.api.model.NotificationSeverity;
import net.croz.nrich.notification.api.service.NotificationMessageResolverService;
import net.croz.nrich.notification.constant.NotificationConstants;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MessageSourceNotificationMessageResolverService implements NotificationMessageResolverService {

    private final MessageSource messageSource;

    private String resolveMessage(final List<String> messageCodeList, final Object[] argumentList, final String defaultMessage) {
        final DefaultMessageSourceResolvable messageCodeResolvable = new DefaultMessageSourceResolvable(messageCodeList.toArray(new String[0]), argumentsWithoutMessageCodeResolvable(argumentList), defaultMessage);

        return messageSource.getMessage(messageCodeResolvable, LocaleContextHolder.getLocale());
    }

    private String resolveMessage(final String messageCode, final String defaultMessage, final Object... arguments) {
        return resolveMessage(messageCode, null, defaultMessage, arguments);
    }

    private String resolveMessage(final String messageCode, final String defaultMessageCode, final String defaultMessage, final Object... arguments) {
        final DefaultMessageSourceResolvable messageSourceResolvable = new DefaultMessageSourceResolvable(new String[] { messageCode, defaultMessageCode }, arguments, defaultMessage);

        return messageSource.getMessage(messageSourceResolvable, LocaleContextHolder.getLocale());
    }

    private String convertToString(final Object[] value) {
        return Arrays.toString(value).replace('[', ' ').replace(']', ' ').trim();
    }
}
