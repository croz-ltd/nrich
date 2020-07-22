package net.croz.nrich.encrypt.api.service;

/**
 * Performs text encryption and decryption.
 */
public interface TextEncryptionService {

    /**
     * Returns encrypted text.
     *
     * @param textToEncrypt text that will be encrypted
     * @return encrypted text
     */
    String encryptText(String textToEncrypt);

    /**
     * Returns decrypted text.
     *
     * @param textToDecrypt text that will be decrypted
     * @return decrypted text
     */
    String decryptText(String textToDecrypt);

}
