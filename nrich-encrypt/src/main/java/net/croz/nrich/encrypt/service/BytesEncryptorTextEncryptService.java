package net.croz.nrich.encrypt.service;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.encrypt.api.service.TextEncryptionService;
import net.croz.nrich.encrypt.exception.EncryptOperationFailedException;
import org.springframework.security.crypto.encrypt.BytesEncryptor;

import java.util.Base64;

@RequiredArgsConstructor
public class BytesEncryptorTextEncryptService implements TextEncryptionService {

    private final BytesEncryptor encryptor;

    private final String charset;

    @Override
    public String encryptText(String textToEncrypt) {
        try {
            byte[] encoded = Base64.getUrlEncoder().encode(encryptor.encrypt(textToEncrypt.getBytes(charset)));

            return new String(encoded, charset);
        }
        catch (Exception exception) {
            throw new EncryptOperationFailedException(String.format("Error occurred during encryption for data: %s", textToEncrypt), exception);
        }
    }

    @Override
    public String decryptText(String textToDecrypt) {
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(textToDecrypt.getBytes(charset));

            return new String(encryptor.decrypt(decoded), charset);
        }
        catch (Exception exception) {
            throw new EncryptOperationFailedException(String.format("Error occurred during decryption for data: %s", textToDecrypt), exception);
        }
    }
}
