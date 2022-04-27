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

package net.croz.nrich.security.csrf.core.constants;

public final class CsrfConstants {

    public static final String EMPTY_PATH = "/";

    public static final String CSRF_CRYPTO_KEY_NAME = "CSRF_CRYPTO_KEY_ATTRIBUTE";

    public static final String CSRF_DEFAULT_PING_URI = "/nrich/csrf/ping";

    public static final String CSRF_PING_STOP_HEADER_NAME = "X-CSRF-StopPing";

    public static final String CSRF_AFTER_LAST_ACTIVE_REQUEST_MILLIS_HEADER_NAME = "X-CSRF-afterLastActiveAppRequestMillis";

    public static final String NRICH_LAST_REAL_API_REQUEST_MILLIS = "NRICH_LAST_REAL_API_REQUEST_MILLIS";

    public static final String CSRF_INITIAL_TOKEN_ATTRIBUTE_NAME = "csrfInitialToken";

    private CsrfConstants() {
    }
}
