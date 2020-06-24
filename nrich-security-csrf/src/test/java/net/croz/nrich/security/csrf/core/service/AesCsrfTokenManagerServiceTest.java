package net.croz.nrich.security.csrf.core.service;

import lombok.SneakyThrows;
import net.croz.nrich.security.csrf.core.exception.CsrfTokenException;
import net.croz.nrich.security.csrf.core.service.stub.TesCsrfTokenHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class AesCsrfTokenManagerServiceTest {

    private static final String CSRF_TOKEN_HEADER_NAME = "X-CSRF-Token";

    private AesCsrfTokenManagerService aesCsrfTokenManagerService;

    @BeforeEach
    void setup() {
        aesCsrfTokenManagerService = new AesCsrfTokenManagerService(Duration.ofMillis(10), Duration.ofMillis(10), CSRF_TOKEN_HEADER_NAME, 128);;
    }

    @Test
    void shouldThrowExceptionWhenTokenIsNotInTokenHolder() {
        // given
        final TesCsrfTokenHolder tokenHolder = new TesCsrfTokenHolder();

        // when
        final Throwable thrown = catchThrowable(() -> aesCsrfTokenManagerService.validateAndRefreshToken(tokenHolder));

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown).isInstanceOf(CsrfTokenException.class);
        assertThat(thrown.getMessage()).isEqualTo("Csrf token is not available!");
    }

    @Test
    void shouldGenerateToken() {
        // given
        final TesCsrfTokenHolder tokenHolder = new TesCsrfTokenHolder();

        // when
        final String token = aesCsrfTokenManagerService.generateToken(tokenHolder);

        // then
        assertThat(token).isNotNull();
    }

    @Test
    void shouldThrowExceptionOnInvalidTokenLength() {
        // given
        final TesCsrfTokenHolder tokenHolder = new TesCsrfTokenHolder();
        tokenHolder.storeToken(CSRF_TOKEN_HEADER_NAME, "nonvalid");

        // when
        final Throwable thrown = catchThrowable(() -> aesCsrfTokenManagerService.validateAndRefreshToken(tokenHolder));

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown).isInstanceOf(CsrfTokenException.class);
        assertThat(thrown.getMessage()).isEqualTo("Csrf token is not valid.");
    }

    @Test
    void shouldThrowExceptionOnInvalidToken() {
        // given
        final TesCsrfTokenHolder tokenHolder = new TesCsrfTokenHolder();
        tokenHolder.storeToken(CSRF_TOKEN_HEADER_NAME, "SqgRJ6bh8uZ4xjpzAUIErg==");

        // when
        final Throwable thrown = catchThrowable(() -> aesCsrfTokenManagerService.validateAndRefreshToken(tokenHolder));

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown).isInstanceOf(CsrfTokenException.class);
        assertThat(thrown.getMessage()).isEqualTo("Csrf token can't be decrypted.");
    }

    @SneakyThrows
    @Test
    void shouldThrowExceptionOnInvalidTokenDuration() {
        // given
        final TesCsrfTokenHolder tokenHolder = new TesCsrfTokenHolder();
        tokenHolder.storeToken(CSRF_TOKEN_HEADER_NAME, aesCsrfTokenManagerService.generateToken(tokenHolder));

        Thread.sleep(30);

        // when
        final Throwable thrown = catchThrowable(() -> aesCsrfTokenManagerService.validateAndRefreshToken(tokenHolder));

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown).isInstanceOf(CsrfTokenException.class);
        assertThat(thrown.getMessage()).isEqualTo("Csrf token is too old.");
    }

}
