package net.croz.nrich.formconfiguration.service;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.formconfiguration.api.model.ConstrainedProperty;
import net.croz.nrich.formconfiguration.constants.FormConfigurationConstants;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.util.StringUtils;
import org.springframework.validation.DefaultMessageCodesResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
public class MessageSourceFieldErrorMessageResolverService implements FieldErrorMessageResolverService {

    private final MessageSource messageSource;

    @Override
    public String resolveErrorMessage(final ConstrainedProperty constrainedProperty, final Locale locale) {
        final List<String> messageCodeList = resolveConstraintMessageCodeList(constrainedProperty);
        final DefaultMessageSourceResolvable defaultMessageSourceResolvable = new DefaultMessageSourceResolvable(messageCodeList.toArray(new String[0]), constrainedProperty.getConstraintArgumentList(), constrainedProperty.getConstraintMessage());

        return messageSource.getMessage(defaultMessageSourceResolvable, locale);
    }

    private List<String> resolveConstraintMessageCodeList(final ConstrainedProperty constrainedProperty) {
        final DefaultMessageCodesResolver messageCodesResolver = new DefaultMessageCodesResolver();
        final String constraintOwningClassName = StringUtils.uncapitalize(constrainedProperty.getParentType().getName());
        final String constraintOwningClassShortName = StringUtils.uncapitalize(constrainedProperty.getParentType().getSimpleName());
        final String constraintPropertyName = constrainedProperty.getName();
        final String constraintName = constrainedProperty.getConstraintName();

        final List<String> codeList = new ArrayList<>();

        codeList.addAll(resolveMessageCodeList(messageCodesResolver, constraintPropertyName, FormConfigurationConstants.CONSTRAINT_FULL_CLIENT_MESSAGE_FORMAT, constraintOwningClassName, constraintPropertyName, constraintName));
        codeList.addAll(resolveMessageCodeList(messageCodesResolver, constraintPropertyName, FormConfigurationConstants.CONSTRAINT_FULL_CLIENT_MESSAGE_FORMAT, constraintOwningClassShortName, constraintPropertyName, constraintName));

        codeList.addAll(resolveMessageCodeList(messageCodesResolver, constraintPropertyName, FormConfigurationConstants.CONSTRAINT_FULL_MESSAGE_FORMAT, constraintOwningClassName, constraintPropertyName, constraintName));
        codeList.addAll(resolveMessageCodeList(messageCodesResolver, constraintPropertyName, FormConfigurationConstants.CONSTRAINT_FULL_MESSAGE_FORMAT, constraintOwningClassShortName, constraintPropertyName, constraintName));

        codeList.addAll(resolveMessageCodeList(messageCodesResolver, constraintPropertyName, FormConfigurationConstants.CONSTRAINT_SHORT_CLIENT_MESSAGE_FORMAT, constraintName));
        codeList.addAll(resolveMessageCodeList(messageCodesResolver, constraintPropertyName, FormConfigurationConstants.CONSTRAINT_SHORT_CLIENT_MESSAGE_FORMAT, constraintName));

        codeList.addAll(resolveMessageCodeList(messageCodesResolver, constraintPropertyName, FormConfigurationConstants.CONSTRAINT_SHORT_MESSAGE_FORMAT, constraintName));
        codeList.addAll(resolveMessageCodeList(messageCodesResolver, constraintPropertyName, FormConfigurationConstants.CONSTRAINT_SHORT_MESSAGE_FORMAT, constraintName));

        return codeList;
    }

    private List<String> resolveMessageCodeList(final DefaultMessageCodesResolver messageCodesResolver, final String constraintPropertyName, final String messageFormat, final String... argumentList) {
        @SuppressWarnings("RedundantCast")
        final String messageCode = String.format(messageFormat, (Object[]) argumentList);

        return Arrays.asList(messageCodesResolver.resolveMessageCodes(messageCode, constraintPropertyName));
    }
}
