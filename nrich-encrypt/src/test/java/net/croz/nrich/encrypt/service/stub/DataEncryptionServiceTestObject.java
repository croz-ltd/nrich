package net.croz.nrich.encrypt.service.stub;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DataEncryptionServiceTestObject {

    private String fieldToEncryptDecrypt;

    private List<String> listToEncrypt;

    private DataEncryptionServiceNestedTestObject nestedTestObject;

    private List<DataEncryptionServiceNestedTestObject> nestedTestObjectList;

}
