package net.croz.nrich.encrypt.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Method encrypt/decrypt configuration.
 */
@RequiredArgsConstructor
@Getter
public class EncryptionConfiguration {

    /**
     * Name of the method to encrypt/decrypt (fullyQualifiedClassName.methodName).
     */
    private final String methodToEncryptDecrypt;

    /**
     * List of property paths to encrypt/decrypt.
     */
    private final List<String> propertyToEncryptDecryptList;

    /**
     * Whether to encrypt method result or decrypt method parameters.
     */
    private final EncryptionOperation encryptionOperation;

}
