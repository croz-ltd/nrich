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

package net.croz.nrich.security.csrf.core.constants;

public final class AesCsrfTokenConstants {

    public static final String ENCRYPTION_ALGORITHM = "AES";

    public static final String CIPHER_TRANSFORMATION = "AES/GCM/NoPadding";

    public static final int AUTHENTICATION_TAG_LENGTH = 128;

    public static final int TOKEN_LENGTH = 34;

    public static final int INITIALIZATION_VECTOR_LENGTH = 12;

    private AesCsrfTokenConstants() {
    }
}
