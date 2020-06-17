package net.croz.nrich.encrypt.service;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.encrypt.api.service.TextEncryptionService;
import net.croz.nrich.encrypt.exception.EncryptionOperationFailedException;
import org.springframework.security.crypto.encrypt.BytesEncryptor;

import java.util.Base64;

@RequiredArgsConstructor
public class BytesEncryptorTextEncryptionService implements TextEncryptionService {

    private final BytesEncryptor encryptor;

    private final String charset;

    @Override
    public String encryptText(final String textToEncrypt) {
        try {
            return Base64.getUrlEncoder().encodeToString(encryptor.encrypt(textToEncrypt.getBytes(charset)));
        }
        catch (final Exception exception) {
            throw new EncryptionOperationFailedException(String.format("Error occurred during encryption for data: %s", textToEncrypt),  exception);
        }
    }

    @Override
    public String decryptText(final String textToDecrypt) {
        try {
            return new String(encryptor.decrypt(Base64.getUrlDecoder().decode(textToDecrypt)), charset);
        }
        catch (final Exception exception) {
            throw new EncryptionOperationFailedException(String.format("Error occurred during decryption for data: %s", textToDecrypt), exception);
        }
    }
}
