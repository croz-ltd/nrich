package net.croz.nrich.encrypt.api.service;

public interface TextEncryptionService {

    String encryptText(String textToEncrypt);

    String decryptText(String textToDecrypt);

}
