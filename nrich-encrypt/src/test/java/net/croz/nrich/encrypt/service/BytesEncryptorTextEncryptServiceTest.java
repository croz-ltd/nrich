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

import net.croz.nrich.encrypt.exception.EncryptOperationFailedException;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.keygen.KeyGenerators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class BytesEncryptorTextEncryptServiceTest {

    private final BytesEncryptor encryptor = Encryptors.standard(KeyGenerators.string().generateKey(), KeyGenerators.string().generateKey());

    private final BytesEncryptorTextEncryptService textEncryptionService = new BytesEncryptorTextEncryptService(encryptor, "UTF-8");

    @Test
    void shouldEncryptDecryptData() {
        // given
        String textToEncrypt = "My text with different characters, for example: čćžšzp []{}%^*()(!@# 1";

        // when
        String encryptedText = textEncryptionService.encryptText(textToEncrypt);

        // then
        assertThat(encryptedText).isNotEqualTo(textToEncrypt);

        // and when
        String decryptedText = textEncryptionService.decryptText(encryptedText);

        // then
        assertThat(decryptedText).isEqualTo(textToEncrypt);
    }

    @Test
    void shouldThrowEncryptionOperationFailedExceptionWhenEncryptingInvalidData() {
        // given
        String textToEncrypt = null;

        // when
        Throwable thrown = catchThrowable(() -> textEncryptionService.encryptText(textToEncrypt));

        // then
        assertThat(thrown).isInstanceOf(EncryptOperationFailedException.class);
    }

    @Test
    void shouldThrowEncryptionOperationFailedExceptionWhenDecryptingInvalidData() {
        // given
        String textToDecrypt = "Non encrypted text";

        // when
        Throwable thrown = catchThrowable(() -> textEncryptionService.decryptText(textToDecrypt));

        // then
        assertThat(thrown).isInstanceOf(EncryptOperationFailedException.class).hasMessageNotContaining(textToDecrypt);
    }

    @Test
    void shouldNotIncludeOriginalTextInExceptionWhenEncryptingFails() {
        // given
        BytesEncryptor throwingEncryptor = mock(BytesEncryptor.class);
        BytesEncryptorTextEncryptService throwingService = new BytesEncryptorTextEncryptService(throwingEncryptor, "UTF-8");
        String textToEncrypt = "secret plaintext value 12345";

        doThrow(new RuntimeException()).when(throwingEncryptor).encrypt(any());

        // when
        Throwable thrown = catchThrowable(() -> throwingService.encryptText(textToEncrypt));

        // then
        assertThat(thrown).isInstanceOf(EncryptOperationFailedException.class).hasMessageNotContaining(textToEncrypt);
    }
}
