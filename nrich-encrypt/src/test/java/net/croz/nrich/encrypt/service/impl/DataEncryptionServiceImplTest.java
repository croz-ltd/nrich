package net.croz.nrich.encrypt.service.impl;

import net.croz.nrich.encrypt.EncryptTestConfiguration;
import net.croz.nrich.encrypt.model.EncryptionContext;
import net.croz.nrich.encrypt.service.stub.DataEncryptionServiceNestedTestObject;
import net.croz.nrich.encrypt.service.stub.DataEncryptionServiceTestObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringJUnitConfig(EncryptTestConfiguration.class)
public class DataEncryptionServiceImplTest {

    @Autowired
    private DataEncryptionServiceImpl dataEncryptionService;

    @Test
    void shouldEncryptSimpleData() {
        // given
        final List<String> propertyList = Collections.singletonList("fieldToEncryptDecrypt");
        final String textToEncrypt = "some text";
        final DataEncryptionServiceTestObject data = new DataEncryptionServiceTestObject();

        data.setFieldToEncryptDecrypt(textToEncrypt);

        // when
        final DataEncryptionServiceTestObject result = dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getFieldToEncryptDecrypt()).isNotEqualTo(textToEncrypt);
    }

    @Test
    void shouldDecryptSimpleData() {
        // given
        final List<String> propertyList = Collections.singletonList("fieldToEncryptDecrypt");
        final String textToEncrypt = "some text";
        final DataEncryptionServiceTestObject data = new DataEncryptionServiceTestObject();

        data.setFieldToEncryptDecrypt(textToEncrypt);

        // when
        final DataEncryptionServiceTestObject result = dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getFieldToEncryptDecrypt()).isNotEqualTo(textToEncrypt);

        // and when
        final DataEncryptionServiceTestObject decryptResult = dataEncryptionService.decryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(decryptResult).isNotNull();
        assertThat(decryptResult.getFieldToEncryptDecrypt()).isEqualTo(textToEncrypt);
    }

    @Test
    void shouldEncryptDecryptCollectionTextData() {
        // given
        final List<String> propertyList = Collections.singletonList("listToEncrypt");
        final List<String> textList = Collections.singletonList("some text");
        final DataEncryptionServiceTestObject data = new DataEncryptionServiceTestObject();

        data.setListToEncrypt(textList);

        // when
        final DataEncryptionServiceTestObject result = dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getListToEncrypt()).isNotEqualTo(textList);

        // and when
        final DataEncryptionServiceTestObject decryptResult = dataEncryptionService.decryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(decryptResult).isNotNull();
        assertThat(decryptResult.getListToEncrypt()).isEqualTo(textList);
    }

    @Test
    void shouldEncryptDecryptNestedTextData() {
        // given
        final List<String> propertyList = Collections.singletonList("nestedTestObject.nestedFieldToEncrypt");
        final String text = "some text";
        final DataEncryptionServiceTestObject data = new DataEncryptionServiceTestObject();

        data.setNestedTestObject(new DataEncryptionServiceNestedTestObject(text, null));

        // when
        final DataEncryptionServiceTestObject result = dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getNestedTestObject().getNestedFieldToEncrypt()).isNotEqualTo(text);

        // and when
        final DataEncryptionServiceTestObject decryptResult = dataEncryptionService.decryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(decryptResult).isNotNull();
        assertThat(decryptResult.getNestedTestObject().getNestedFieldToEncrypt()).isEqualTo(text);
    }

    @Test
    void shouldEncryptDecryptNestedCollectionTextData() {
        // given
        final List<String> propertyList = Collections.singletonList("nestedTestObjectList.nestedFieldToEncrypt");
        final String text = "some text";
        final DataEncryptionServiceTestObject data = new DataEncryptionServiceTestObject();

        data.setNestedTestObjectList(Collections.singletonList(new DataEncryptionServiceNestedTestObject(text, null)));

        // when
        final DataEncryptionServiceTestObject result = dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getNestedTestObjectList()).extracting("nestedFieldToEncrypt").doesNotContain(text);

        // and when
        final DataEncryptionServiceTestObject decryptResult = dataEncryptionService.decryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(decryptResult).isNotNull();
        assertThat(decryptResult.getNestedTestObjectList()).extracting("nestedFieldToEncrypt").containsExactly(text);
    }

    @Test
    void shouldNotFailOnNullValues() {
        // given
        final List<String> propertyList = Arrays.asList("fieldToEncryptDecrypt", "listToEncrypt", "nestedTestObject.nestedFieldToEncrypt", "nestedTestObjectList.nestedFieldToEncrypt");
        final DataEncryptionServiceTestObject data = new DataEncryptionServiceTestObject();

        // then
        assertThatCode(() -> dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build())).doesNotThrowAnyException();

        // then
        assertThatCode(() -> dataEncryptionService.decryptData(data, propertyList, EncryptionContext.builder().build())).doesNotThrowAnyException();
    }

    @Test
    void shouldNotFailOnNestedNullValues() {
        // given
        final List<String> propertyList = Collections.singletonList("nestedTestObject.parent.nestedFieldToEncrypt");
        final DataEncryptionServiceTestObject data = new DataEncryptionServiceTestObject();

        // then
        assertThatCode(() -> dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build())).doesNotThrowAnyException();

        // then
        assertThatCode(() -> dataEncryptionService.decryptData(data, propertyList, EncryptionContext.builder().build())).doesNotThrowAnyException();
    }

    @Test
    void shouldNotFailOnPropertiesThatDontExist() {
        // given
        final List<String> propertyList = Collections.singletonList("nonExistingProperty");
        final DataEncryptionServiceTestObject data = new DataEncryptionServiceTestObject();

        // then
        assertThatCode(() -> dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build())).doesNotThrowAnyException();

        // then
        assertThatCode(() -> dataEncryptionService.decryptData(data, propertyList, EncryptionContext.builder().build())).doesNotThrowAnyException();
    }

    @Test
    void shouldNotFailOnNestedPropertiesThatDontExist() {
        // given
        final List<String> propertyList = Collections.singletonList("nestedTestObject.nonExistingProperty");
        final DataEncryptionServiceTestObject data = new DataEncryptionServiceTestObject();

        // then
        assertThatCode(() -> dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build())).doesNotThrowAnyException();

        // then
        assertThatCode(() -> dataEncryptionService.decryptData(data, propertyList, EncryptionContext.builder().build())).doesNotThrowAnyException();
    }

    @Test
    void shouldNotFailOnNullValueWhenEncryptingData() {
        // given
        final List<String> propertyList = Collections.singletonList("fieldToEncryptDecrypt");
        final DataEncryptionServiceTestObject data = null;

        // when
        final DataEncryptionServiceTestObject result = dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(result).isNull();
    }

    @Test
    void shouldNotFailOnNullValueWhenDecryptingData() {
        // given
        final List<String> propertyList = Collections.singletonList("fieldToEncryptDecrypt");
        final DataEncryptionServiceTestObject data = null;

        // when
        final DataEncryptionServiceTestObject result = dataEncryptionService.decryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(result).isNull();
    }
}
