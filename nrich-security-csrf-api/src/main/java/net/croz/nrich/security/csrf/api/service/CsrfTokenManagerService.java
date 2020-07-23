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
