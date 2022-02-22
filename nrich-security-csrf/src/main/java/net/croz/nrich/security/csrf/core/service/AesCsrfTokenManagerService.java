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

    public void validateAndRefreshToken(CsrfTokenKeyHolder csrfTokenKeyHolder) {
        String csrfToken = csrfTokenKeyHolder.getToken();

        if (csrfToken == null) {
            throw new CsrfTokenException("Csrf token is not available!");
        }

        Key cryptoKey = fetchCryptoKey(csrfTokenKeyHolder, cryptoKeyLength);

        validateToken(csrfToken, cryptoKey);

        csrfTokenKeyHolder.storeToken(generateToken(cryptoKey));
    }

    public String generateToken(CsrfTokenKeyHolder csrfTokenKeyHolder) {
        Key cryptoKey = fetchCryptoKey(csrfTokenKeyHolder, cryptoKeyLength);

        return generateToken(cryptoKey);
    }

    private Key fetchCryptoKey(CsrfTokenKeyHolder csrfTokenKeyHolder, Integer cryptoKeyLength) {
        Key cryptoKey = csrfTokenKeyHolder.getCryptoKey();

        if (cryptoKey == null) {
            cryptoKey = generateCryptoKey(cryptoKeyLength);
            csrfTokenKeyHolder.storeCryptoKey(cryptoKey);
        }

        return cryptoKey;
    }

    private Key generateCryptoKey(Integer cryptoKeyLength) {
        KeyGenerator keyGenerator = getKeyGenerator();

        keyGenerator.init(cryptoKeyLength);

        return keyGenerator.generateKey();
    }

    private void validateToken(String csrfToken, Key key) {
        byte[] csrfTokenEncryptedBytes = Base64.getUrlDecoder().decode(csrfToken);

        if (csrfTokenEncryptedBytes.length != 16) {
            throw new CsrfTokenException("Csrf token is not valid.");
        }

        Cipher cipher = initCipher(key, Cipher.DECRYPT_MODE);
        byte[] csrfTokenDecryptedBytes;
        try {
            csrfTokenDecryptedBytes = cipher.doFinal(csrfTokenEncryptedBytes);
        }
        catch (Exception exception) {
            throw new CsrfTokenException("Csrf token can't be decrypted.", exception);
        }

        Long csrfTokenTimeMillis = new BigInteger(csrfTokenDecryptedBytes).longValue();
        Long currentTimeMillis = Instant.now().toEpochMilli();

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

    private String generateToken(Key key) {
        long time = Instant.now().toEpochMilli();
        byte[] currentTimeBytes = BigInteger.valueOf(time).toByteArray();

        byte[] encryptedCurrentTimeBytes = encryptedCurrentTimeBytes(key, currentTimeBytes);

        return new String(Base64.getUrlEncoder().encode(encryptedCurrentTimeBytes), StandardCharsets.ISO_8859_1);
    }

    @SneakyThrows
    private Cipher initCipher(Key key, Integer mode) {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);

        cipher.init(mode, key);

        return cipher;
    }

    @SneakyThrows
    private KeyGenerator getKeyGenerator() {
        return KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
    }

    @SneakyThrows
    private byte[] encryptedCurrentTimeBytes(Key key, byte[] currentTimeBytes) {
        Cipher cipher = initCipher(key, Cipher.ENCRYPT_MODE);

        return cipher.doFinal(currentTimeBytes);
    }
}
