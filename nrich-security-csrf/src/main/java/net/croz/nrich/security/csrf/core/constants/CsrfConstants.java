package net.croz.nrich.security.csrf.core.constants;

public final class CsrfConstants {

    public static final String CSRF_TOKEN_HEADER_NAME = "X-CSRF-Token";

    public static final Integer CSRF_DEFAULT_CRYPTO_KEY_LENGTH = 128;

    public static final String CSRF_CRYPTO_KEY_SESSION_ATTRIBUTE_NAME = "CSRF_CRYPTO_KEY_SESSION_ATTRIBUTE";

    public static final Integer CSRF_TOKEN_DEFAULT_EXPIRATION_INTERVAL_MILLIS = 35 * 60 * 1000;

    public static final Integer CSRF_TOKEN_DEFAULT_FUTURE_THRESHOLD_MILLIS = 60 * 1000;

    public static final String CSRF_DEFAULT_PING_URI = "/nrich/csrf/ping";

    public static final String CSRF_PING_STOP_HEADER_NAME = "X-CSRF-StopPing";

    public static final String CSRF_AFTER_LAST_ACTIVE_REQUEST_MILLIS_HEADER_NAME = "X-CSRF-afterLastActiveAppRequestMillis";

    public static final String NRICH_LAST_REAL_API_REQUEST_MILLIS = "NRICH_LAST_REAL_API_REQUEST_MILLIS";

    public static final String CSRF_INITIAL_TOKEN_ATTRIBUTE_NAME = "csrfInitialToken";

    private CsrfConstants() {
    }
}
