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
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class DefaultDataEncryptService implements DataEncryptionService {

    private final TextEncryptionService textEncryptionService;

    @Override
    public <T> T encryptData(T data, List<String> pathToEncryptDecryptList, EncryptionContext encryptionContext) {
        if (data == null) {
            return null;
        }

        if (CollectionUtils.isEmpty(pathToEncryptDecryptList) && data instanceof String) {
            @SuppressWarnings("unchecked")
            T encryptedText = (T) encryptDecryptText(encryptionContext, (String) data, EncryptionOperation.ENCRYPT);

            return encryptedText;
        }

        pathToEncryptDecryptList.forEach(path -> executeEncryptionOperation(encryptionContext, data, path, EncryptionOperation.ENCRYPT));

        return data;
    }

    @Override
    public <T> T decryptData(T data, List<String> pathToEncryptDecryptList, EncryptionContext encryptionContext) {
        if (data == null) {
            return null;
        }

        if (CollectionUtils.isEmpty(pathToEncryptDecryptList) && data instanceof String) {
            @SuppressWarnings("unchecked")
            T decryptedText = (T) encryptDecryptText(encryptionContext, (String) data, EncryptionOperation.DECRYPT);

            return decryptedText;
        }

        pathToEncryptDecryptList.forEach(path -> executeEncryptionOperation(encryptionContext, data, path, EncryptionOperation.DECRYPT));

        return data;
    }

    private void executeEncryptionOperation(EncryptionContext encryptionContext, Object object, String targetPath, EncryptionOperation operation) {
        String[] pathList = targetPath.split(EncryptConstants.PATH_SEPARATOR_REGEX);

        if (pathList.length > 1) {
            String[] remainingPath = Arrays.copyOfRange(pathList, 0, pathList.length - 1);

            encryptDecryptNestedValue(encryptionContext, getPropertyValueByPath(object, String.join(EncryptConstants.PATH_SEPARATOR, remainingPath)), pathList[pathList.length - 1], operation);
        }
        else {
            encryptDecryptNestedValue(encryptionContext, object, targetPath, operation);
        }
    }

    private void encryptDecryptNestedValue(EncryptionContext encryptionContext, Object objectContainingFieldsToEncryptOrDecrypt, String propertyName, EncryptionOperation operation) {
        if (objectContainingFieldsToEncryptOrDecrypt != null && propertyName != null) {
            if (objectContainingFieldsToEncryptOrDecrypt instanceof Collection) {
                ((Collection<?>) objectContainingFieldsToEncryptOrDecrypt).forEach(value -> encryptDecryptValue(encryptionContext, value, propertyName, operation));
            }
            else {
                encryptDecryptValue(encryptionContext, objectContainingFieldsToEncryptOrDecrypt, propertyName, operation);
            }
        }
    }

    private void encryptDecryptValue(EncryptionContext encryptionContext, Object objectContainingFieldsToEncryptOrDecrypt, String propertyName, EncryptionOperation operation) {
        Object value = getPropertyValueByPath(objectContainingFieldsToEncryptOrDecrypt, propertyName);

        if (value instanceof String) {
            String textToEncryptOrDecrypt = (String) value;
            String encryptedValue = encryptDecryptText(encryptionContext, textToEncryptOrDecrypt, operation);

            setPropertyValueByPath(objectContainingFieldsToEncryptOrDecrypt, propertyName, encryptedValue);

        }
        else if (value instanceof Collection && ((Collection<?>) value).stream().allMatch(String.class::isInstance)) {
            @SuppressWarnings("unchecked")
            Collection<String> textToEncryptOrDecryptList = (Collection<String>) value;

            Collection<String> encryptedValueList = textToEncryptOrDecryptList.stream()
                .map(textToEncryptOrDecrypt -> encryptDecryptText(encryptionContext, textToEncryptOrDecrypt, operation))
                .collect(Collectors.toList());

            setPropertyValueByPath(objectContainingFieldsToEncryptOrDecrypt, propertyName, encryptedValueList);
        }
    }

    @SuppressWarnings("unchecked")
    protected Object getPropertyValueByPath(Object holder, String path) {
        if (holder instanceof Map) {
            return ((Map<String, Object>) holder).get(path);
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

    private String encryptDecryptText(EncryptionContext encryptionContext, String text, EncryptionOperation operation) {
        log.debug("Starting encryption operation: {} for method: {}", operation.name(), encryptionContext.getFullyQualifiedMethodName());

        return operation == EncryptionOperation.ENCRYPT ? textEncryptionService.encryptText(text) : textEncryptionService.decryptText(text);
    }
}
