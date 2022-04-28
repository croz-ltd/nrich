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

package net.croz.nrich.security.csrf.api.holder;

import java.security.Key;

/**
 * Holder for CSRF tokens and encryption keys.
 */
public interface CsrfTokenKeyHolder {

    /**
     * Returns CSRF token or null if no token is present.
     *
     * @return CSRF token
     */
    String getToken();

    /**
     * Stores CSRF token.
     *
     * @param csrfToken token to store.
     */
    void storeToken(String csrfToken);

    /**
     * Returns key that was used to encrypt a CSRF token or null if no key is available.
     *
     * @return crypto key
     */
    Key getCryptoKey();

    /**
     * Stores crypto key
     *
     * @param cryptoKey crypto key to store
     */
    void storeCryptoKey(Key cryptoKey);

}
