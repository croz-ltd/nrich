/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package net.croz.nrich.encrypt.service;

import net.croz.nrich.encrypt.EncryptTestConfiguration;
import net.croz.nrich.encrypt.api.model.EncryptionContext;
import net.croz.nrich.encrypt.service.stub.DataEncryptionServiceArrayTestObject;
import net.croz.nrich.encrypt.service.stub.DataEncryptionServiceNestedTestObject;
import net.croz.nrich.encrypt.service.stub.DataEncryptionServiceTestObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringJUnitConfig(EncryptTestConfiguration.class)
class DefaultDataEncryptServiceTest {

    private static final String TEXT_TO_ENCRYPT_DECRYPT = "some text";

    @Autowired
    private DefaultDataEncryptService dataEncryptionService;

    @Test
    void shouldEncryptSimpleData() {
        // given
        List<String> propertyList = Collections.singletonList("propertyToEncryptDecrypt");
        DataEncryptionServiceTestObject data = new DataEncryptionServiceTestObject();

        data.setPropertyToEncryptDecrypt(TEXT_TO_ENCRYPT_DECRYPT);

        // when
        DataEncryptionServiceTestObject result = dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPropertyToEncryptDecrypt()).isNotEqualTo(TEXT_TO_ENCRYPT_DECRYPT);
    }

    @Test
    void shouldDecryptSimpleData() {
        // given
        List<String> propertyList = Collections.singletonList("propertyToEncryptDecrypt");
        DataEncryptionServiceTestObject data = new DataEncryptionServiceTestObject();

        data.setPropertyToEncryptDecrypt(TEXT_TO_ENCRYPT_DECRYPT);

        // when
        DataEncryptionServiceTestObject result = dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPropertyToEncryptDecrypt()).isNotEqualTo(TEXT_TO_ENCRYPT_DECRYPT);

        // and when
        DataEncryptionServiceTestObject decryptResult = dataEncryptionService.decryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(decryptResult).isNotNull();
        assertThat(decryptResult.getPropertyToEncryptDecrypt()).isEqualTo(TEXT_TO_ENCRYPT_DECRYPT);
    }

    @Test
    void shouldEncryptDecryptCollectionTextData() {
        // given
        List<String> propertyList = Collections.singletonList("listToEncrypt");
        List<String> textList = Collections.singletonList("some text");
        DataEncryptionServiceTestObject data = new DataEncryptionServiceTestObject();

        data.setListToEncrypt(textList);

        // when
        DataEncryptionServiceTestObject result = dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getListToEncrypt()).isNotEqualTo(textList);

        // and when
        DataEncryptionServiceTestObject decryptResult = dataEncryptionService.decryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(decryptResult).isNotNull();
        assertThat(decryptResult.getListToEncrypt()).isEqualTo(textList);
    }

    @Test
    void shouldEncryptDecryptNestedTextData() {
        // given
        List<String> propertyList = Collections.singletonList("nestedTestObject.nestedFieldToEncrypt");
        String text = "some text";
        DataEncryptionServiceTestObject data = new DataEncryptionServiceTestObject();

        data.setNestedTestObject(new DataEncryptionServiceNestedTestObject(text, null));

        // when
        DataEncryptionServiceTestObject result = dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getNestedTestObject().getNestedFieldToEncrypt()).isNotEqualTo(text);

        // and when
        DataEncryptionServiceTestObject decryptResult = dataEncryptionService.decryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(decryptResult).isNotNull();
        assertThat(decryptResult.getNestedTestObject().getNestedFieldToEncrypt()).isEqualTo(text);
    }

    @Test
    void shouldEncryptDecryptNestedCollectionTextData() {
        // given
        List<String> propertyList = Collections.singletonList("nestedTestObjectList.nestedFieldToEncrypt");
        String text = "some text";
        DataEncryptionServiceTestObject data = new DataEncryptionServiceTestObject();

        data.setNestedTestObjectList(Collections.singletonList(new DataEncryptionServiceNestedTestObject(text, null)));

        // when
        DataEncryptionServiceTestObject result = dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getNestedTestObjectList()).extracting("nestedFieldToEncrypt").doesNotContain(text);

        // and when
        DataEncryptionServiceTestObject decryptResult = dataEncryptionService.decryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(decryptResult).isNotNull();
        assertThat(decryptResult.getNestedTestObjectList()).extracting("nestedFieldToEncrypt").containsExactly(text);
    }

    @Test
    void shouldNotFailOnNullValues() {
        // given
        List<String> propertyList = Arrays.asList("propertyToEncryptDecrypt", "listToEncrypt", "nestedTestObject.nestedFieldToEncrypt", "nestedTestObjectList.nestedFieldToEncrypt");
        DataEncryptionServiceTestObject data = new DataEncryptionServiceTestObject();

        // then
        assertThatCode(() -> dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build())).doesNotThrowAnyException();

        // then
        assertThatCode(() -> dataEncryptionService.decryptData(data, propertyList, EncryptionContext.builder().build())).doesNotThrowAnyException();
    }

    @ValueSource(strings = { "nestedTestObject.parent.nestedFieldToEncrypt", "nonExistingProperty", "nestedTestObject.nonExistingProperty" })
    @ParameterizedTest
    void shouldNotFailOnNestedNullOrInvalidValues() {
        // given
        List<String> propertyList = Collections.singletonList("nestedTestObject.parent.nestedFieldToEncrypt");
        DataEncryptionServiceTestObject data = new DataEncryptionServiceTestObject();

        // then
        assertThatCode(() -> dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build())).doesNotThrowAnyException();

        // then
        assertThatCode(() -> dataEncryptionService.decryptData(data, propertyList, EncryptionContext.builder().build())).doesNotThrowAnyException();
    }

    @Test
    void shouldNotFailOnNullValueWhenEncryptingData() {
        // given
        List<String> propertyList = Collections.singletonList("propertyToEncryptDecrypt");
        DataEncryptionServiceTestObject data = null;

        // when
        DataEncryptionServiceTestObject result = dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(result).isNull();
    }

    @Test
    void shouldNotFailOnNullValueWhenDecryptingData() {
        // given
        List<String> propertyList = Collections.singletonList("propertyToEncryptDecrypt");
        DataEncryptionServiceTestObject data = null;

        // when
        DataEncryptionServiceTestObject result = dataEncryptionService.decryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(result).isNull();
    }

    @Test
    void shouldEncryptDecryptMapData() {
        // given
        String key = "mapKey";
        List<String> propertyList = Collections.singletonList(key);
        Map<String, String> data = new HashMap<>();

        data.put(key, TEXT_TO_ENCRYPT_DECRYPT);

        // when
        Map<String, String> result = dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(result).doesNotContainEntry(key, TEXT_TO_ENCRYPT_DECRYPT);

        // and when
        Map<String, String> decryptResult = dataEncryptionService.decryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(decryptResult).containsEntry(key, TEXT_TO_ENCRYPT_DECRYPT);
    }

    @Test
    void shouldEncryptDecryptNestedMapData() {
        // given
        String key = "mapKey";
        List<String> propertyList = Collections.singletonList("mapKey.mapKey");
        Map<String, Map<String, String>> data = new HashMap<>();
        Map<String, String> nestedData = new HashMap<>();

        nestedData.put(key, TEXT_TO_ENCRYPT_DECRYPT);

        data.put(key, nestedData);

        // when
        Map<String, Map<String, String>> result = dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(result).isNotNull();
        assertThat(result.get(key)).doesNotContainEntry(key, TEXT_TO_ENCRYPT_DECRYPT);

        // and when
        Map<String, Map<String, String>> decryptResult = dataEncryptionService.decryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(decryptResult).isNotNull();
        assertThat(result.get(key)).containsEntry(key, TEXT_TO_ENCRYPT_DECRYPT);
    }

    @Test
    void shouldEncryptDecryptArrayData() {
        // given
        DataEncryptionServiceArrayTestObject testObject = new DataEncryptionServiceArrayTestObject();
        List<String> propertyList = Arrays.asList("propertyEncryptDecrypt", "arrayEncrypt");
        DataEncryptionServiceArrayTestObject[] data = new DataEncryptionServiceArrayTestObject[] { testObject };

        testObject.setArrayEncrypt(new String[] { TEXT_TO_ENCRYPT_DECRYPT });
        testObject.setPropertyEncryptDecrypt(TEXT_TO_ENCRYPT_DECRYPT);

        // when
        DataEncryptionServiceArrayTestObject[] result = dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(result).isNotNull();
        assertThat(result[0].getPropertyEncryptDecrypt()).isNotEqualTo(TEXT_TO_ENCRYPT_DECRYPT);
        assertThat(result[0].getArrayEncrypt()).isNotEmpty();
        assertThat(result[0].getArrayEncrypt()[0]).isNotEqualTo(TEXT_TO_ENCRYPT_DECRYPT);

        // and when
        DataEncryptionServiceArrayTestObject[] decryptResult = dataEncryptionService.decryptData(result, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(decryptResult).usingRecursiveComparison().isEqualTo(data);
    }

    @Test
    void shouldNotFailWithUnsupportedTypes() {
        // given
        int[] data = new int[] { 1 };
        List<String> propertyList = Collections.singletonList("property");

        // when
        Throwable thrown = catchThrowable(() -> dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build()));

        // then
        assertThat(thrown).isNull();
    }

    @Test
    void shouldEncryptDecryptStringCollection() {
        // given
        List<String> propertyList = Collections.emptyList();
        List<String> data = Collections.singletonList(TEXT_TO_ENCRYPT_DECRYPT);

        // when
        List<String> result = dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(result).isNotEmpty().doesNotContain(TEXT_TO_ENCRYPT_DECRYPT);

        // and when
        List<String> decryptResult = dataEncryptionService.decryptData(result, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(decryptResult).containsExactly(TEXT_TO_ENCRYPT_DECRYPT);
    }

    @Test
    void shouldEncryptDecryptStringArray() {
        // given
        List<String> propertyList = Collections.emptyList();
        String[] data = new String[] { TEXT_TO_ENCRYPT_DECRYPT };

        // when
        String[] result = dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(result).isNotEmpty().doesNotContain(TEXT_TO_ENCRYPT_DECRYPT);

        // and when
        String[] decryptResult = dataEncryptionService.decryptData(result, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(decryptResult).containsExactly(TEXT_TO_ENCRYPT_DECRYPT);
    }

    @Test
    void shouldEncryptDecryptStringSet() {
        // given
        List<String> propertyList = Collections.emptyList();
        Set<String> data = new HashSet<>(Collections.singleton(TEXT_TO_ENCRYPT_DECRYPT));

        // when
        Set<String> result = dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(result).isNotEmpty().doesNotContain(TEXT_TO_ENCRYPT_DECRYPT);

        // and when
        Set<String> decryptResult = dataEncryptionService.decryptData(result, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(decryptResult).containsExactly(TEXT_TO_ENCRYPT_DECRYPT);
    }

    @Test
    void shouldNotFailForNonStringCollectionArray() {
        // given
        Object[] data = new Object[] { "value", Integer.MAX_VALUE };

        // when
        Throwable thrown = catchThrowable(() -> dataEncryptionService.encryptData(data, Collections.emptyList(), EncryptionContext.builder().build()));

        // then
        assertThat(thrown).isNull();
    }
}
