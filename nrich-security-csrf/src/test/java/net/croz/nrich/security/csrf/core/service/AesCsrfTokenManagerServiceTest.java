package net.croz.nrich.security.csrf.core.service;

import net.croz.nrich.security.csrf.core.constants.CsrfConstants;
import net.croz.nrich.security.csrf.core.exception.CsrfTokenException;
import net.croz.nrich.security.csrf.core.service.stub.TesCsrfTokenKeyHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class AesCsrfTokenManagerServiceTest {

    private static final String CSRF_TOKEN_KEY_NAME = "X-CSRF-Token";

    private AesCsrfTokenManagerService aesCsrfTokenManagerService;

    @BeforeEach
    void setup() {
        aesCsrfTokenManagerService = new AesCsrfTokenManagerService(Duration.ofMillis(10), Duration.ofMillis(10), 128);
    }

    @Test
    void shouldThrowExceptionWhenTokenIsNotInTokenHolder() {
        // given
        TesCsrfTokenKeyHolder tokenHolder = new TesCsrfTokenKeyHolder(CSRF_TOKEN_KEY_NAME, CsrfConstants.CSRF_CRYPTO_KEY_NAME);

        // when
        Throwable thrown = catchThrowable(() -> aesCsrfTokenManagerService.validateAndRefreshToken(tokenHolder));

        // then
        assertThat(thrown).isInstanceOf(CsrfTokenException.class);
        assertThat(thrown.getMessage()).isEqualTo("Csrf token is not available!");
    }

    @Test
    void shouldGenerateToken() {
        // given
        TesCsrfTokenKeyHolder tokenHolder = new TesCsrfTokenKeyHolder(CSRF_TOKEN_KEY_NAME, CsrfConstants.CSRF_CRYPTO_KEY_NAME);

        // when
        String token = aesCsrfTokenManagerService.generateToken(tokenHolder);

        // then
        assertThat(token).isNotNull();
    }

    @Test
    void shouldThrowExceptionOnInvalidTokenLength() {
        // given
        TesCsrfTokenKeyHolder tokenHolder = new TesCsrfTokenKeyHolder(CSRF_TOKEN_KEY_NAME, CsrfConstants.CSRF_CRYPTO_KEY_NAME);
        tokenHolder.storeToken("nonvalid");

        // when
        Throwable thrown = catchThrowable(() -> aesCsrfTokenManagerService.validateAndRefreshToken(tokenHolder));

        // then
        assertThat(thrown).isInstanceOf(CsrfTokenException.class);
        assertThat(thrown.getMessage()).isEqualTo("Csrf token is not valid.");
    }

    @Test
    void shouldThrowExceptionOnInvalidToken() {
        // given
        TesCsrfTokenKeyHolder tokenHolder = new TesCsrfTokenKeyHolder(CSRF_TOKEN_KEY_NAME, CsrfConstants.CSRF_CRYPTO_KEY_NAME);
        tokenHolder.storeToken("SqgRJ6bh8uZ4xjpzAUIErg==");

        // when
        Throwable thrown = catchThrowable(() -> aesCsrfTokenManagerService.validateAndRefreshToken(tokenHolder));

        // then
        assertThat(thrown).isInstanceOf(CsrfTokenException.class);
        assertThat(thrown.getMessage()).isEqualTo("Csrf token can't be decrypted.");
    }

    @Test
    void shouldThrowExceptionOnInvalidTokenDuration() throws Exception {
        // given
        TesCsrfTokenKeyHolder tokenHolder = new TesCsrfTokenKeyHolder(CSRF_TOKEN_KEY_NAME, CsrfConstants.CSRF_CRYPTO_KEY_NAME);
        tokenHolder.storeToken(aesCsrfTokenManagerService.generateToken(tokenHolder));

        Thread.sleep(30);

        // when
        Throwable thrown = catchThrowable(() -> aesCsrfTokenManagerService.validateAndRefreshToken(tokenHolder));

        // then
        assertThat(thrown).isInstanceOf(CsrfTokenException.class);
        assertThat(thrown.getMessage()).isEqualTo("Csrf token is too old.");
    }

}
