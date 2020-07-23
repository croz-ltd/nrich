package net.croz.nrich.security.csrf.core.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.croz.nrich.security.csrf.api.holder.CsrfTokenKeyHolder;
import net.croz.nrich.security.csrf.api.service.CsrfTokenManagerService;
import net.croz.nrich.security.csrf.core.exception.CsrfTokenException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@RequiredArgsConstructor
public class AesCsrfTokenManagerService implements CsrfTokenManagerService {

    private static final String ENCRYPTION_ALGORITHM = "AES";

    private final Duration tokenExpirationInterval;

    private final Duration tokenFutureThreshold;

    private final Integer cryptoKeyLength;

    public void validateAndRefreshToken(final CsrfTokenKeyHolder csrfTokenKeyHolder) {
        final String csrfToken = csrfTokenKeyHolder.getToken();

        if (csrfToken == null) {
            throw new CsrfTokenException("Csrf token is not available!");
        }

        final Key cryptoKey = fetchCryptoKey(csrfTokenKeyHolder, cryptoKeyLength);

        validateToken(csrfToken, cryptoKey);

        csrfTokenKeyHolder.storeToken(generateToken(cryptoKey));
    }

    public String generateToken(final CsrfTokenKeyHolder csrfTokenKeyHolder) {
        final Key cryptoKey = fetchCryptoKey(csrfTokenKeyHolder, cryptoKeyLength);

        return generateToken(cryptoKey);
    }

    private Key fetchCryptoKey(final CsrfTokenKeyHolder csrfTokenKeyHolder, final Integer cryptoKeyLength) {
        Key cryptoKey = csrfTokenKeyHolder.getCryptoKey();

        if (cryptoKey == null) {
            cryptoKey = generateCryptoKey(cryptoKeyLength);
            csrfTokenKeyHolder.storeCryptoKey(cryptoKey);
        }

        return cryptoKey;
    }

    private Key generateCryptoKey(final Integer cryptoKeyLength) {
        final KeyGenerator keyGenerator = getKeyGenerator();

        keyGenerator.init(cryptoKeyLength);

        return keyGenerator.generateKey();
    }

    private void validateToken(final String csrfToken, final Key key) {
        final byte[] csrfTokenEncryptedBytes = Base64.getUrlDecoder().decode(csrfToken);

        if (csrfTokenEncryptedBytes.length != 16) {
            throw new CsrfTokenException("Csrf token is not valid.");
        }

        final Cipher cipher = initCipher(key, Cipher.DECRYPT_MODE);
        final byte[] csrfTokenDecryptedBytes;
        try {
            csrfTokenDecryptedBytes = cipher.doFinal(csrfTokenEncryptedBytes);
        }
        catch (final Exception exception) {
            throw new CsrfTokenException("Csrf token can't be decrypted.", exception);
        }

        final Long csrfTokenTimeMillis = new BigInteger(csrfTokenDecryptedBytes).longValue();
        final Long currentTimeMillis = Instant.now().toEpochMilli();

        // If token time is in future, tolerate that case to some extent (for example, to avoid issues around unsynchronized computer times in cluster)
        if (currentTimeMillis < csrfTokenTimeMillis) {
            if (csrfTokenTimeMillis - currentTimeMillis > tokenFutureThreshold.toMillis()) {
                throw new CsrfTokenException("Csrf token is too far in the future.");
            }
        }
        else if (currentTimeMillis - csrfTokenTimeMillis > tokenExpirationInterval.toMillis()) {
            throw new CsrfTokenException("Csrf token is too old.");
        }

    }

    private String generateToken(final Key key) {
        final long time = Instant.now().toEpochMilli();
        final byte[] currentTimeBytes = BigInteger.valueOf(time).toByteArray();

        final byte[] encryptedCurrentTimeBytes = encryptedCurrentTimeBytes(key, currentTimeBytes);

        return new String(Base64.getUrlEncoder().encode(encryptedCurrentTimeBytes), StandardCharsets.ISO_8859_1);
    }

    @SneakyThrows
    private Cipher initCipher(final Key key, final Integer mode) {
        final Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);

        cipher.init(mode, key);

        return cipher;
    }

    @SneakyThrows
    private KeyGenerator getKeyGenerator() {
        return KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
    }

    @SneakyThrows
    private byte[] encryptedCurrentTimeBytes(final Key key, final byte[] currentTimeBytes) {
        final Cipher cipher = initCipher(key, Cipher.ENCRYPT_MODE);

        return cipher.doFinal(currentTimeBytes);
    }
}
