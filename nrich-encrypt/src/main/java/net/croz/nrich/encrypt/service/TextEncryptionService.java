package net.croz.nrich.encrypt.service;

public interface TextEncryptionService {

    String encryptText(String textToEncrypt);

    String decryptText(String textToDecrypt);

}
