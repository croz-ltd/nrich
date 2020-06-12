package net.croz.nrich.encrypt.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class EncryptionConfiguration {

    private final String methodToEncryptDecrypt;

    private final List<String> propertyToEncryptDecryptList;

    private final EncryptionOperation encryptionOperation;

}
