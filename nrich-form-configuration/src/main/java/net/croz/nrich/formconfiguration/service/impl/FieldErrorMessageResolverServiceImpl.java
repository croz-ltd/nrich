package net.croz.nrich.formconfiguration.service.impl;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.formconfiguration.model.ConstrainedProperty;
import net.croz.nrich.formconfiguration.service.FieldErrorMessageResolverService;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.DefaultMessageCodesResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
public class FieldErrorMessageResolverServiceImpl implements FieldErrorMessageResolverService {

    private static final String CONSTRAINT_FULL_CLIENT_MESSAGE_FORMAT = "%s.%s.client.%s.invalid";

    private static final String CONSTRAINT_FULL_MESSAGE_FORMAT = "%s.%s.%s.invalid";

    private static final String CONSTRAINT_SHORT_CLIENT_MESSAGE_FORMAT = "client.%s.invalid";

    private static final String CONSTRAINT_SHORT_MESSAGE_FORMAT = "%s.invalid";

    private final MessageSource messageSource;

    @Override
    public String resolveErrorMessage(final ConstrainedProperty constrainedProperty, final Locale locale) {
        final List<String> messageCodeList = resolveConstraintMessageCodeList(constrainedProperty);
        final DefaultMessageSourceResolvable defaultMessageSourceResolvable = new DefaultMessageSourceResolvable(messageCodeList.toArray(new String[0]), constrainedProperty.getConstraintArgumentList(), "");

        return messageSource.getMessage(defaultMessageSourceResolvable, locale);
    }

    private List<String> resolveConstraintMessageCodeList(final ConstrainedProperty request) {
        final DefaultMessageCodesResolver messageCodesResolver = new DefaultMessageCodesResolver();
        final String constraintOwningClassName = unCapitalize(request.getParentType().getName());
        final String constraintOwningClassShortName = unCapitalize(request.getParentType().getSimpleName());
        final String constraintPropertyName = request.getName();
        final String constraintName = request.getConstraintName();

        final List<String> codeList = new ArrayList<>();

        codeList.addAll(resolveMessageCodeList(messageCodesResolver, constraintPropertyName, CONSTRAINT_FULL_CLIENT_MESSAGE_FORMAT, constraintOwningClassName, constraintPropertyName, constraintName));
        codeList.addAll(resolveMessageCodeList(messageCodesResolver, constraintPropertyName, CONSTRAINT_FULL_CLIENT_MESSAGE_FORMAT, constraintOwningClassShortName, constraintPropertyName, constraintName));

        codeList.addAll(resolveMessageCodeList(messageCodesResolver, constraintPropertyName, CONSTRAINT_FULL_MESSAGE_FORMAT, constraintOwningClassName, constraintPropertyName, constraintName));
        codeList.addAll(resolveMessageCodeList(messageCodesResolver, constraintPropertyName, CONSTRAINT_FULL_MESSAGE_FORMAT, constraintOwningClassShortName, constraintPropertyName, constraintName));

        codeList.addAll(resolveMessageCodeList(messageCodesResolver, constraintPropertyName, CONSTRAINT_SHORT_CLIENT_MESSAGE_FORMAT, constraintName));
        codeList.addAll(resolveMessageCodeList(messageCodesResolver, constraintPropertyName, CONSTRAINT_SHORT_CLIENT_MESSAGE_FORMAT, constraintName));

        codeList.addAll(resolveMessageCodeList(messageCodesResolver, constraintPropertyName, CONSTRAINT_SHORT_MESSAGE_FORMAT, constraintName));
        codeList.addAll(resolveMessageCodeList(messageCodesResolver, constraintPropertyName, CONSTRAINT_SHORT_MESSAGE_FORMAT, constraintName));

        return codeList;
    }

    private List<String> resolveMessageCodeList(final DefaultMessageCodesResolver messageCodesResolver, final String constraintPropertyName, final String messageFormat, final String... argumentList) {
        @SuppressWarnings("RedundantCast")
        final String messageCode = String.format(messageFormat, (Object[]) argumentList);

        return Arrays.asList(messageCodesResolver.resolveMessageCodes(messageCode, constraintPropertyName));
    }

    private String unCapitalize(final String name) {
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }
}
