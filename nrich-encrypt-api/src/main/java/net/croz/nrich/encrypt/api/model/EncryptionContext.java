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

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Context in which encryption operation is performed
 */
@Getter
@Builder
public class EncryptionContext {

    /**
     * Name of the method to encrypt/decrypt (in following form: fullyQualifiedClassName.methodName).
     */
    private final String fullyQualifiedMethodName;

    /**
     * Method arguments.
     */
    private final List<Object> methodArguments;

    /**
     * Decrypted method arguments (same as methodArguments for decrypt operation).
     */
    private final List<Object> methodDecryptedArguments;

}
