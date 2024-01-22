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

package net.croz.nrich.encrypt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.croz.nrich.encrypt.api.model.EncryptionContext;
import net.croz.nrich.encrypt.api.model.EncryptionOperation;
import net.croz.nrich.encrypt.api.service.DataEncryptionService;
import net.croz.nrich.encrypt.api.service.TextEncryptionService;
import net.croz.nrich.encrypt.constants.EncryptConstants;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class DefaultDataEncryptService implements DataEncryptionService {

    private final TextEncryptionService textEncryptionService;

    @Override
    public <T> T encryptData(T data, List<String> pathToEncryptDecryptList, EncryptionContext encryptionContext) {
        return encryptDecryptData(data, pathToEncryptDecryptList, encryptionContext, EncryptionOperation.ENCRYPT);
    }

    @Override
    public <T> T decryptData(T data, List<String> pathToEncryptDecryptList, EncryptionContext encryptionContext) {
        return encryptDecryptData(data, pathToEncryptDecryptList, encryptionContext, EncryptionOperation.DECRYPT);
    }

    protected <T> T encryptDecryptData(T data, List<String> pathToEncryptDecryptList, EncryptionContext encryptionContext, EncryptionOperation encryptionOperation) {
        if (data == null) {
            return null;
        }

        if (CollectionUtils.isEmpty(pathToEncryptDecryptList)) {
            @SuppressWarnings("unchecked")
            T encryptedDecryptedValue = (T) encryptDecryptValue(encryptionContext, data, encryptionOperation);

            return encryptedDecryptedValue == null ? data : encryptedDecryptedValue;
        }

        pathToEncryptDecryptList.forEach(path -> executeEncryptionOperation(encryptionContext, data, path, encryptionOperation));

        return data;
    }

    protected void executeEncryptionOperation(EncryptionContext encryptionContext, Object object, String targetPath, EncryptionOperation operation) {
        String[] pathList = targetPath.split(EncryptConstants.PATH_SEPARATOR_REGEX);

        if (pathList.length > 1) {
            String[] remainingPath = Arrays.copyOfRange(pathList, 0, pathList.length - 1);

            encryptDecryptNestedValue(encryptionContext, getPropertyValueByPath(object, String.join(EncryptConstants.PATH_SEPARATOR, remainingPath)), pathList[pathList.length - 1], operation);
        }
        else {
            encryptDecryptNestedValue(encryptionContext, object, targetPath, operation);
        }
    }

    protected void encryptDecryptNestedValue(EncryptionContext encryptionContext, Object objectContainingFieldsToEncryptOrDecrypt, String propertyName, EncryptionOperation operation) {
        if (objectContainingFieldsToEncryptOrDecrypt != null && propertyName != null) {
            if (objectContainingFieldsToEncryptOrDecrypt instanceof Collection<?> collection) {
                collection.forEach(value -> encryptDecryptValue(encryptionContext, value, propertyName, operation));
            }
            else if (objectContainingFieldsToEncryptOrDecrypt instanceof Object[] objectArray) {
                Arrays.stream(objectArray).forEach(value -> encryptDecryptValue(encryptionContext, value, propertyName, operation));
            }
            else {
                encryptDecryptValue(encryptionContext, objectContainingFieldsToEncryptOrDecrypt, propertyName, operation);
            }
        }
    }

    protected void encryptDecryptValue(EncryptionContext encryptionContext, Object objectContainingFieldsToEncryptOrDecrypt, String propertyName, EncryptionOperation operation) {
        Object value = getPropertyValueByPath(objectContainingFieldsToEncryptOrDecrypt, propertyName);
        Object result = encryptDecryptValue(encryptionContext, value, operation);

        if (result == null) {
            return;
        }

        setPropertyValueByPath(objectContainingFieldsToEncryptOrDecrypt, propertyName, result);
    }

    protected Object encryptDecryptValue(EncryptionContext encryptionContext, Object value, EncryptionOperation operation) {
        if (value == null) {
            return null;
        }

        if (value instanceof String textToEncryptOrDecrypt) {
            return encryptDecryptText(encryptionContext, textToEncryptOrDecrypt, operation);
        }
        else if (isSupportedCollection(value) && ((Collection<?>) value).stream().allMatch(String.class::isInstance)) {
            @SuppressWarnings("unchecked")
            Collection<String> textToEncryptOrDecryptList = (Collection<String>) value;
            Collector<? super String, ?, ?> collector = value instanceof Set ? Collectors.toSet() : Collectors.toList();

            return textToEncryptOrDecryptList.stream()
                .map(textToEncryptOrDecrypt -> encryptDecryptText(encryptionContext, textToEncryptOrDecrypt, operation))
                .collect(collector);
        }
        else if (value instanceof String[] textToEncryptOrDecryptList) {
            return Arrays.stream(textToEncryptOrDecryptList)
                .map(textToEncryptOrDecrypt -> encryptDecryptText(encryptionContext, textToEncryptOrDecrypt, operation))
                .toArray(String[]::new);
        }

        log.warn("Unable to {} property, check if the specified path is correct and if the specified property is a string", operation.name().toLowerCase());

        return null;
    }

    protected Object getPropertyValueByPath(Object holder, String path) {
        if (holder instanceof Map<?, ?> map) {
            return map.get(path);
        }

        try {
            return PropertyAccessorFactory.forDirectFieldAccess(holder).getPropertyValue(path);
        }
        catch (Exception ignored) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    protected void setPropertyValueByPath(Object holder, String path, Object value) {
        if (holder instanceof Map) {
            ((Map<String, Object>) holder).put(path, value);
        }
        else {
            PropertyAccessorFactory.forDirectFieldAccess(holder).setPropertyValue(path, value);
        }
    }

    protected String encryptDecryptText(EncryptionContext encryptionContext, String text, EncryptionOperation operation) {
        log.debug("Starting encryption operation: {} for method: {}", operation.name(), encryptionContext.getFullyQualifiedMethodName());

        return operation == EncryptionOperation.ENCRYPT ? textEncryptionService.encryptText(text) : textEncryptionService.decryptText(text);
    }

    private boolean isSupportedCollection(Object value) {
        return value instanceof List || value instanceof Set;
    }
}
