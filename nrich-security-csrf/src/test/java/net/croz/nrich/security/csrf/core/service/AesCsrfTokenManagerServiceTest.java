package net.croz.nrich.security.csrf.core.service;

import net.croz.nrich.security.csrf.core.constants.CsrfConstants;
import net.croz.nrich.security.csrf.core.exception.CsrfTokenException;
import net.croz.nrich.security.csrf.core.service.stub.TesCsrfTokenKeyHolder;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.awaitility.Awaitility.await;

class AesCsrfTokenManagerServiceTest {

    private static final String CSRF_TOKEN_KEY_NAME = "X-CSRF-Token";

    private static final Duration DEFAULT_DURATION = Duration.ofMillis(100);

    private final AesCsrfTokenManagerService aesCsrfTokenManagerService = new AesCsrfTokenManagerService(DEFAULT_DURATION, DEFAULT_DURATION, 128);

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
    void shouldNotFailValidationOnGeneratedToken() {
        // given
        TesCsrfTokenKeyHolder tokenHolder = new TesCsrfTokenKeyHolder(CSRF_TOKEN_KEY_NAME, CsrfConstants.CSRF_CRYPTO_KEY_NAME);

        tokenHolder.storeToken(aesCsrfTokenManagerService.generateToken(tokenHolder));

        // when
        Throwable thrown = catchThrowable(() -> aesCsrfTokenManagerService.validateAndRefreshToken(tokenHolder));

        // then
        assertThat(thrown).isNull();
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
        tokenHolder.storeToken("dGLml7ib_mTsNYz_RwKsa-AsOVLFuVCsDQmkolvUZcpd1g==");

        // when
        Throwable thrown = catchThrowable(() -> aesCsrfTokenManagerService.validateAndRefreshToken(tokenHolder));

        // then
        assertThat(thrown).isInstanceOf(CsrfTokenException.class);
        assertThat(thrown.getMessage()).isEqualTo("Csrf token can't be decrypted.");
    }

    @Test
    void shouldThrowExceptionOnInvalidTokenDuration() {
        // given
        TesCsrfTokenKeyHolder tokenHolder = new TesCsrfTokenKeyHolder(CSRF_TOKEN_KEY_NAME, CsrfConstants.CSRF_CRYPTO_KEY_NAME);
        tokenHolder.storeToken(aesCsrfTokenManagerService.generateToken(tokenHolder));

        await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
            // when
            Throwable thrown = catchThrowable(() -> aesCsrfTokenManagerService.validateAndRefreshToken(tokenHolder));

            // then
            assertThat(thrown).isInstanceOf(CsrfTokenException.class);
            assertThat(thrown.getMessage()).isEqualTo("Csrf token is too old.");
        });
    }
}
