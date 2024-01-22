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

package net.croz.nrich.encrypt.api.model;

import java.util.List;

/**
 * Method encrypt/decrypt configuration.
 *
 * @param methodToEncryptDecrypt       Name of the method to encrypt/decrypt (fullyQualifiedClassName.methodName).
 * @param propertyToEncryptDecryptList List of property paths to encrypt/decrypt.
 * @param encryptionOperation          Whether to encrypt method result or decrypt method parameters.
 */
public record EncryptionConfiguration(String methodToEncryptDecrypt, List<String> propertyToEncryptDecryptList, EncryptionOperation encryptionOperation) {

}
