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
