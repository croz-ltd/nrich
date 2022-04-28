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
import net.croz.nrich.encrypt.api.service.TextEncryptionService;
import net.croz.nrich.encrypt.exception.EncryptOperationFailedException;
import org.springframework.security.crypto.encrypt.BytesEncryptor;

import java.util.Base64;

@RequiredArgsConstructor
public class BytesEncryptorTextEncryptService implements TextEncryptionService {

    private final BytesEncryptor encryptor;

    private final String charset;

    @Override
    public String encryptText(String textToEncrypt) {
        try {
            byte[] encoded = Base64.getUrlEncoder().encode(encryptor.encrypt(textToEncrypt.getBytes(charset)));

            return new String(encoded, charset);
        }
        catch (Exception exception) {
            throw new EncryptOperationFailedException(String.format("Error occurred during encryption for data: %s", textToEncrypt), exception);
        }
    }

    @Override
    public String decryptText(String textToDecrypt) {
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(textToDecrypt.getBytes(charset));

            return new String(encryptor.decrypt(decoded), charset);
        }
        catch (Exception exception) {
            throw new EncryptOperationFailedException(String.format("Error occurred during decryption for data: %s", textToDecrypt), exception);
        }
    }
}
