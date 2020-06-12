package net.croz.nrich.encrypt.service.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class DataEncryptionServiceNestedTestObject {

    private final String nestedFieldToEncrypt;

    private final DataEncryptionServiceNestedTestObject parent;

}
