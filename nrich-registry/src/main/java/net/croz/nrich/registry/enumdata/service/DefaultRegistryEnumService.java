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

package net.croz.nrich.registry.enumdata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.croz.nrich.registry.api.enumdata.model.EnumResult;
import net.croz.nrich.registry.api.enumdata.request.ListBulkRegistryEnumRequest;
import net.croz.nrich.registry.api.enumdata.request.ListRegistryEnumRequest;
import net.croz.nrich.registry.api.enumdata.service.RegistryEnumService;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class DefaultRegistryEnumService implements RegistryEnumService {

    private static final String ADDITIONAL_METHODS_FOR_SERIALIZATION_PROPERTY_NAME = "ADDITIONAL_METHODS_FOR_SERIALIZATION";

    private static final String ENUM_DESCRIPTION_MESSAGE_FORMAT = "%s.%s.description";

    private final MessageSource messageSource;

    @Override
    public Map<String, List<EnumResult>> listBulk(ListBulkRegistryEnumRequest request) {
        return request.getRegistryRequestList().stream().collect(Collectors.toMap(ListRegistryEnumRequest::getClassFullName, this::list));
    }

    @Override
    public List<EnumResult> list(ListRegistryEnumRequest request) {
        boolean isEmptyQuery = !StringUtils.hasText(request.getQuery());
        List<EnumResult> enumResults = loadEnumRegistry(request.getClassFullName(), LocaleContextHolder.getLocale());

        List<EnumResult> foundEnums = new ArrayList<>();
        enumResults.forEach(enumResult -> {
            if (isEmptyQuery || enumResult.getDescription().toLowerCase().contains(request.getQuery().toLowerCase())) {
                foundEnums.add(enumResult);
            }
        });

        return foundEnums;
    }

    private List<EnumResult> loadEnumRegistry(String enumClassName, Locale locale) {
        Class<? extends Enum<?>> enumType = loadEnumClass(enumClassName);

        if (enumType == null || !Enum.class.isAssignableFrom(enumType)) {
            return Collections.emptyList();
        }

        List<String> additionalRegistryMethodsForSerialization = findAdditionalMethodsForSerialization(enumType);

        return Arrays.stream(enumType.getEnumConstants())
            .map(enumValue -> {
                String description = resolveMessage(String.format(ENUM_DESCRIPTION_MESSAGE_FORMAT, enumClassName, enumValue.name()), enumValue.name(), locale);
                Map<String, Object> additionalEnumData = serializeAdditionalEnumData(enumType, enumValue, additionalRegistryMethodsForSerialization);

                return new EnumResult(enumClassName, description, enumValue, additionalEnumData);
            })
            .toList();
    }

    private String resolveMessage(String code, String defaultValue, Locale locale) {
        String message;
        try {
            message = messageSource.getMessage(code, null, locale);
        }
        catch (NoSuchMessageException exception) {
            log.debug("Message not found for enum under code: {}, returning default value: {}", code, defaultValue);
            message = defaultValue;
        }

        return message;
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Enum<?>> loadEnumClass(String enumClassName) {
        try {
            return (Class<? extends Enum<?>>) Class.forName(enumClassName, true, Thread.currentThread().getContextClassLoader());
        }
        catch (ClassNotFoundException ignored) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> findAdditionalMethodsForSerialization(Class<? extends Enum<?>> enumType) {
        Field field = ReflectionUtils.findField(enumType, ADDITIONAL_METHODS_FOR_SERIALIZATION_PROPERTY_NAME);

        if (field == null) {
            return Collections.emptyList();
        }

        return (List<String>) ReflectionUtils.getField(field, null);
    }

    private Map<String, Object> serializeAdditionalEnumData(Class<?> enumType, Enum<?> enumValue, List<String> additionalRegistryMethodsForSerialization) {
        Map<String, Object> result = new HashMap<>();
        additionalRegistryMethodsForSerialization.forEach(methodName -> {
            Object methodResult = invokeMethodIfExists(enumType, enumValue, methodName);
            if (methodResult == null) {
                return;
            }

            result.put(methodName, methodResult);
        });

        return result;
    }

    private Object invokeMethodIfExists(Class<?> enumType, Object target, String methodName) {
        Method method = ReflectionUtils.findMethod(enumType, methodName);
        if (method == null) {
            return null;
        }

        return ReflectionUtils.invokeMethod(method, target);
    }
}
