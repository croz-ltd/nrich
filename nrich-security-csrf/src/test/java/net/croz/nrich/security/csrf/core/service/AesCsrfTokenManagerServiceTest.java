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

package net.croz.nrich.security.csrf.core.service;

import net.croz.nrich.security.csrf.core.constants.CsrfConstants;
import net.croz.nrich.security.csrf.core.exception.CsrfTokenException;
import net.croz.nrich.security.csrf.core.service.stub.TesCsrfTokenKeyHolder;
import org.junit.jupiter.api.Test;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class AesCsrfTokenManagerServiceTest {

    private static final String CSRF_TOKEN_KEY_NAME = "X-CSRF-Token";

    private static final Duration DEFAULT_DURATION = Duration.ofMillis(100);

    private static final int CRYPTO_KEY_LENGTH = 128;

    private final AesCsrfTokenManagerService aesCsrfTokenManagerService = new AesCsrfTokenManagerService(DEFAULT_DURATION, DEFAULT_DURATION, CRYPTO_KEY_LENGTH);

    @Test
    void shouldThrowExceptionWhenTokenIsNotInTokenHolder() {
        // given
        TesCsrfTokenKeyHolder tokenHolder = new TesCsrfTokenKeyHolder(CSRF_TOKEN_KEY_NAME, CsrfConstants.CSRF_CRYPTO_KEY_NAME);

        // when
        Throwable thrown = catchThrowable(() -> aesCsrfTokenManagerService.validateAndRefreshToken(tokenHolder));

        // then
        assertThat(thrown).isInstanceOf(CsrfTokenException.class).hasMessage("Csrf token is not available!");
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
        assertThat(thrown).isInstanceOf(CsrfTokenException.class).hasMessage("Csrf token is not valid.");
    }

    @Test
    void shouldThrowExceptionOnInvalidToken() {
        // given
        TesCsrfTokenKeyHolder tokenHolder = new TesCsrfTokenKeyHolder(CSRF_TOKEN_KEY_NAME, CsrfConstants.CSRF_CRYPTO_KEY_NAME);
        tokenHolder.storeToken("dGLml7ib_mTsNYz_RwKsa-AsOVLFuVCsDQmkolvUZcpd1g==");

        // when
        Throwable thrown = catchThrowable(() -> aesCsrfTokenManagerService.validateAndRefreshToken(tokenHolder));

        // then
        assertThat(thrown).isInstanceOf(CsrfTokenException.class).hasMessage("Csrf token can't be decrypted.");
    }

    @Test
    void shouldThrowExceptionOnInvalidTokenDuration() {
        // given
        AesCsrfTokenManagerService aesCsrfTokenManagerServiceWithTokenDurationInPast = new AesCsrfTokenManagerService(Duration.ofSeconds(-1), Duration.ofSeconds(-1), CRYPTO_KEY_LENGTH);
        TesCsrfTokenKeyHolder tokenHolder = new TesCsrfTokenKeyHolder(CSRF_TOKEN_KEY_NAME, CsrfConstants.CSRF_CRYPTO_KEY_NAME);

        tokenHolder.storeToken(aesCsrfTokenManagerServiceWithTokenDurationInPast.generateToken(tokenHolder));

        // when
        Throwable thrown = catchThrowable(() -> aesCsrfTokenManagerServiceWithTokenDurationInPast.validateAndRefreshToken(tokenHolder));

        // then
        assertThat(thrown).isInstanceOf(CsrfTokenException.class).hasMessage("Csrf token is too old.");
    }

    @Test
    void shouldThrowExceptionWhenTokenIsTooFarInTheFuture() {
        // given
        TesCsrfTokenKeyHolder tokenHolder = new TesCsrfTokenKeyHolder(CSRF_TOKEN_KEY_NAME, CsrfConstants.CSRF_CRYPTO_KEY_NAME);
        SecretKeySpec key = new SecretKeySpec("ABCDEFGHIJKLMNOP".getBytes(StandardCharsets.UTF_8), "AES");

        tokenHolder.storeCryptoKey(key);
        tokenHolder.storeToken("08ARlOyaVTyEbdXtCB2rFoPj0r1jC6EIFXrK_7HqNb-XMg==");

        // when
        Throwable thrown = catchThrowable(() -> aesCsrfTokenManagerService.validateAndRefreshToken(tokenHolder));

        // then
        assertThat(thrown).isInstanceOf(CsrfTokenException.class).hasMessage("Csrf token is too far in the future.");
    }
}
