/*
 *  Copyright 2020-2022 CROZ d.o.o, the original author or authors.
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

package net.croz.nrich.formconfiguration.service;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.formconfiguration.api.model.ConstrainedProperty;
import net.croz.nrich.formconfiguration.constants.FormConfigurationConstants;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
public class MessageSourceFieldErrorMessageResolverService implements FieldErrorMessageResolverService {

    private final MessageSource messageSource;

    @Override
    public String resolveErrorMessage(ConstrainedProperty constrainedProperty, Locale locale) {
        List<String> messageCodeList = resolveConstraintMessageCodeList(constrainedProperty);
        Object[] argumentList = convertArraysInArgumentList(constrainedProperty.getConstraintArgumentList());
        String message = constrainedProperty.getConstraintMessage();
        DefaultMessageSourceResolvable defaultMessageSourceResolvable = new DefaultMessageSourceResolvable(messageCodeList.toArray(new String[0]), argumentList, message);

        return messageSource.getMessage(defaultMessageSourceResolvable, locale);
    }

    private List<String> resolveConstraintMessageCodeList(ConstrainedProperty constrainedProperty) {
        String constraintOwningClassName = StringUtils.uncapitalize(constrainedProperty.getParentType().getName());
        String constraintOwningClassShortName = StringUtils.uncapitalize(constrainedProperty.getParentType().getSimpleName());
        String constraintPropertyName = constrainedProperty.getName();
        String constraintName = constrainedProperty.getConstraintName();

        List<String> codeList = new ArrayList<>();

        codeList.add(resolveMessageCode(FormConfigurationConstants.CONSTRAINT_FULL_CLIENT_MESSAGE_FORMAT, constraintOwningClassName, constraintPropertyName, constraintName));
        codeList.add(resolveMessageCode(FormConfigurationConstants.CONSTRAINT_FULL_CLIENT_MESSAGE_FORMAT, constraintOwningClassShortName, constraintPropertyName, constraintName));

        codeList.add(resolveMessageCode(FormConfigurationConstants.CONSTRAINT_FULL_MESSAGE_FORMAT, constraintOwningClassName, constraintPropertyName, constraintName));
        codeList.add(resolveMessageCode(FormConfigurationConstants.CONSTRAINT_FULL_MESSAGE_FORMAT, constraintOwningClassShortName, constraintPropertyName, constraintName));

        codeList.add(resolveMessageCode(FormConfigurationConstants.CONSTRAINT_MEDIUM_CLIENT_MESSAGE_FORMAT, constraintPropertyName, constraintName));
        codeList.add(resolveMessageCode(FormConfigurationConstants.CONSTRAINT_MEDIUM_MESSAGE_FORMAT, constraintPropertyName, constraintName));

        codeList.add(resolveMessageCode(FormConfigurationConstants.CONSTRAINT_SHORT_CLIENT_MESSAGE_FORMAT, constraintName));
        codeList.add(resolveMessageCode(FormConfigurationConstants.CONSTRAINT_SHORT_MESSAGE_FORMAT, constraintName));

        return codeList;
    }

    private String resolveMessageCode(String messageFormat, String... argumentList) {
        return String.format(messageFormat, (Object[]) argumentList);
    }

    private Object[] convertArraysInArgumentList(Object[] argumentList) {
        if (argumentList == null) {
            return new Object[0];
        }

        return Arrays.stream(argumentList)
            .map(value -> value instanceof Object[] ? convertToString((Object[]) value) : value)
            .toArray();
    }

    private String convertToString(Object[] value) {
        return Arrays.toString(value).replace('[', ' ').replace(']', ' ').trim();
    }
}
