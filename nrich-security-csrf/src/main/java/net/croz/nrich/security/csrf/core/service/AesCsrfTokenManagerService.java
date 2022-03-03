package net.croz.nrich.security.csrf.core.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.croz.nrich.security.csrf.api.holder.CsrfTokenKeyHolder;
import net.croz.nrich.security.csrf.api.service.CsrfTokenManagerService;
import net.croz.nrich.security.csrf.core.constants.AesCsrfTokenConstants;
import net.croz.nrich.security.csrf.core.exception.CsrfTokenException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.GCMParameterSpec;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@RequiredArgsConstructor
public class AesCsrfTokenManagerService implements CsrfTokenManagerService {

    private static final SecureRandom INITIALIZATION_VECTOR_RANDOM = new SecureRandom();

    private final Duration tokenExpirationInterval;

    private final Duration tokenFutureThreshold;

    private final Integer cryptoKeyLength;

    public void validateAndRefreshToken(CsrfTokenKeyHolder csrfTokenKeyHolder) {
        String csrfToken = csrfTokenKeyHolder.getToken();

        if (csrfToken == null) {
            throw new CsrfTokenException("Csrf token is not available!");
        }

        Key cryptoKey = fetchCryptoKey(csrfTokenKeyHolder);

        validateToken(csrfToken, cryptoKey);

        csrfTokenKeyHolder.storeToken(generateToken(cryptoKey));
    }

    public String generateToken(CsrfTokenKeyHolder csrfTokenKeyHolder) {
        Key cryptoKey = fetchCryptoKey(csrfTokenKeyHolder);

        return generateToken(cryptoKey);
    }

    private Key fetchCryptoKey(CsrfTokenKeyHolder csrfTokenKeyHolder) {
        Key cryptoKey = csrfTokenKeyHolder.getCryptoKey();

        if (cryptoKey == null) {
            cryptoKey = generateCryptoKey();
            csrfTokenKeyHolder.storeCryptoKey(cryptoKey);
        }

        return cryptoKey;
    }

    private Key generateCryptoKey() {
        KeyGenerator keyGenerator = getKeyGenerator();

        keyGenerator.init(cryptoKeyLength);

        return keyGenerator.generateKey();
    }

    private void validateToken(String csrfToken, Key key) {
        byte[] csrfTokenEncryptedBytes = Base64.getUrlDecoder().decode(csrfToken);

        if (csrfTokenEncryptedBytes.length != AesCsrfTokenConstants.TOKEN_LENGTH) {
            throw new CsrfTokenException("Csrf token is not valid.");
        }

        byte[] decryptedCurrentTimeBytes = decryptedCurrentTimeBytes(key, csrfTokenEncryptedBytes);

        Long csrfTokenTimeMillis = new BigInteger(decryptedCurrentTimeBytes).longValue();
        Long currentTimeMillis = Instant.now().toEpochMilli();

        // If token time is in the future, tolerate that case to some extent (for example, to avoid issues around unsynchronized computer times in cluster)
        if (currentTimeMillis < csrfTokenTimeMillis) {
            if (csrfTokenTimeMillis - currentTimeMillis > tokenFutureThreshold.toMillis()) {
                throw new CsrfTokenException("Csrf token is too far in the future.");
            }
        }
        else if (currentTimeMillis - csrfTokenTimeMillis > tokenExpirationInterval.toMillis()) {
            throw new CsrfTokenException("Csrf token is too old.");
        }
    }

    private byte[] decryptedCurrentTimeBytes(Key key, byte[] csrfTokenEncryptedBytes) {
        GCMParameterSpec parameterSpec = new GCMParameterSpec(AesCsrfTokenConstants.AUTHENTICATION_TAG_LENGTH, csrfTokenEncryptedBytes, 0, AesCsrfTokenConstants.INITIALIZATION_VECTOR_LENGTH);
        Cipher cipher = initCipher(key, Cipher.DECRYPT_MODE, parameterSpec);

        byte[] csrfTokenDecryptedBytes;
        try {
            int inputLength = csrfTokenEncryptedBytes.length - AesCsrfTokenConstants.INITIALIZATION_VECTOR_LENGTH;

            csrfTokenDecryptedBytes = cipher.doFinal(csrfTokenEncryptedBytes, AesCsrfTokenConstants.INITIALIZATION_VECTOR_LENGTH, inputLength);
        }
        catch (Exception exception) {
            throw new CsrfTokenException("Csrf token can't be decrypted.", exception);
        }

        return csrfTokenDecryptedBytes;
    }

    private String generateToken(Key key) {
        long time = Instant.now().toEpochMilli();
        byte[] currentTimeBytes = BigInteger.valueOf(time).toByteArray();

        byte[] encryptedCurrentTimeBytes = encryptedCurrentTimeBytes(key, currentTimeBytes);

        return new String(Base64.getUrlEncoder().encode(encryptedCurrentTimeBytes), StandardCharsets.ISO_8859_1);
    }

    @SneakyThrows
    private KeyGenerator getKeyGenerator() {
        return KeyGenerator.getInstance(AesCsrfTokenConstants.ENCRYPTION_ALGORITHM);
    }

    @SneakyThrows
    private byte[] encryptedCurrentTimeBytes(Key key, byte[] currentTimeBytes) {
        GCMParameterSpec parameterSpec = new GCMParameterSpec(AesCsrfTokenConstants.AUTHENTICATION_TAG_LENGTH, generateInitializationVector());
        Cipher cipher = initCipher(key, Cipher.ENCRYPT_MODE, parameterSpec);

        byte[] encryptedCurrentTimeInBytes = cipher.doFinal(currentTimeBytes);
        byte[] initializationVector = cipher.getIV();

        return ByteBuffer.allocate(initializationVector.length + encryptedCurrentTimeInBytes.length)
            .put(initializationVector)
            .put(encryptedCurrentTimeInBytes)
            .array();
    }

    @SneakyThrows
    private Cipher initCipher(Key key, Integer mode, GCMParameterSpec parameterSpec) {
        Cipher cipher = Cipher.getInstance(AesCsrfTokenConstants.CIPHER_TRANSFORMATION);

        cipher.init(mode, key, parameterSpec);

        return cipher;
    }

    private byte[] generateInitializationVector() {
        byte[] initializationVector = new byte[AesCsrfTokenConstants.INITIALIZATION_VECTOR_LENGTH];

        INITIALIZATION_VECTOR_RANDOM.nextBytes(initializationVector);

        return initializationVector;
    }
}
