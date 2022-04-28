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

package net.croz.nrich.encrypt.api.service;

/**
 * Performs text encryption and decryption.
 */
public interface TextEncryptionService {

    /**
     * Returns encrypted text.
     *
     * @param textToEncrypt text that will be encrypted
     * @return encrypted text
     */
    String encryptText(String textToEncrypt);

    /**
     * Returns decrypted text.
     *
     * @param textToDecrypt text that will be decrypted
     * @return decrypted text
     */
    String decryptText(String textToDecrypt);

}
