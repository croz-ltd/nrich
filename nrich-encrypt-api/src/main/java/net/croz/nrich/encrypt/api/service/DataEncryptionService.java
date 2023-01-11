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

package net.croz.nrich.encrypt.api.service;

import net.croz.nrich.encrypt.api.model.EncryptionContext;

import java.util.List;

/**
 * Resolves string values to be encrypted/decrypted from data by path list and delegates encryption operation to {@link TextEncryptionService}.
 */
public interface DataEncryptionService {

    /**
     * Returns data with encrypted values matching path list.
     *
     * @param data                     object holding values that will be encrypted (can be a collection)
     * @param pathToEncryptDecryptList list of paths on data object that will be encrypted
     * @param encryptionContext        context in which encryption operation is performed
     * @param <T>                      type of data
     * @return data object with encrypted values
     */
    <T> T encryptData(T data, List<String> pathToEncryptDecryptList, EncryptionContext encryptionContext);

    /**
     * Returns data with decrypted values matching path list.
     *
     * @param data                     object holding values that will be decrypted (can be a collection)
     * @param pathToEncryptDecryptList list of paths on data object that will be decrypted
     * @param encryptionContext        context in which encryption operation is performed
     * @param <T>                      type of data
     * @return data object with decrypted values
     */
    <T> T decryptData(T data, List<String> pathToEncryptDecryptList, EncryptionContext encryptionContext);

}
