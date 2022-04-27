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

package net.croz.nrich.security.csrf.api.service;

import net.croz.nrich.security.csrf.api.holder.CsrfTokenKeyHolder;

/**
 * Generates, validates and refreshes CSRF tokens. Uses {@link CsrfTokenKeyHolder} instances for storing and resolving tokens.
 */
public interface CsrfTokenManagerService {

    /**
     * Validates and refreshes CSRF token. Encryption key is resolved from {@link CsrfTokenKeyHolder} if key is null a new key is generated and
     * stored. If token is missing or invalid a CsrfTokenException is thrown.
     *
     * @param csrfTokenKeyHolder holder for csrf tokens and encryption keys
     */
    void validateAndRefreshToken(CsrfTokenKeyHolder csrfTokenKeyHolder);

    /**
     * Generates token from key resolved from {@link CsrfTokenKeyHolder} if key is null a new key is generated and stored.
     *
     * @param csrfTokenKeyHolder holder for csrf tokens and encryption keys
     * @return generated CSRF token.
     */
    String generateToken(CsrfTokenKeyHolder csrfTokenKeyHolder);

}
