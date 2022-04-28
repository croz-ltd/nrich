/*
 *  Copyright 2020-2022 CROZ d.o.o, the original author or authors.
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
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringJUnitConfig(EncryptTestConfiguration.class)
class DefaultDataEncryptServiceTest {

    @Autowired
    private DefaultDataEncryptService dataEncryptionService;

    @Test
    void shouldEncryptSimpleData() {
        // given
        List<String> propertyList = Collections.singletonList("fieldToEncryptDecrypt");
        String textToEncrypt = "some text";
        DataEncryptionServiceTestObject data = new DataEncryptionServiceTestObject();

        data.setFieldToEncryptDecrypt(textToEncrypt);

        // when
        DataEncryptionServiceTestObject result = dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getFieldToEncryptDecrypt()).isNotEqualTo(textToEncrypt);
    }

    @Test
    void shouldDecryptSimpleData() {
        // given
        List<String> propertyList = Collections.singletonList("fieldToEncryptDecrypt");
        String textToEncrypt = "some text";
        DataEncryptionServiceTestObject data = new DataEncryptionServiceTestObject();

        data.setFieldToEncryptDecrypt(textToEncrypt);

        // when
        DataEncryptionServiceTestObject result = dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getFieldToEncryptDecrypt()).isNotEqualTo(textToEncrypt);

        // and when
        DataEncryptionServiceTestObject decryptResult = dataEncryptionService.decryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(decryptResult).isNotNull();
        assertThat(decryptResult.getFieldToEncryptDecrypt()).isEqualTo(textToEncrypt);
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
        List<String> propertyList = Arrays.asList("fieldToEncryptDecrypt", "listToEncrypt", "nestedTestObject.nestedFieldToEncrypt", "nestedTestObjectList.nestedFieldToEncrypt");
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
        List<String> propertyList = Collections.singletonList("fieldToEncryptDecrypt");
        DataEncryptionServiceTestObject data = null;

        // when
        DataEncryptionServiceTestObject result = dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(result).isNull();
    }

    @Test
    void shouldNotFailOnNullValueWhenDecryptingData() {
        // given
        List<String> propertyList = Collections.singletonList("fieldToEncryptDecrypt");
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
        String textToEncrypt = "some text";
        Map<String, String> data = new HashMap<>();

        data.put(key, textToEncrypt);

        // when
        Map<String, String> result = dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(result).doesNotContainEntry(key, textToEncrypt);

        // and when
        Map<String, String> decryptResult = dataEncryptionService.decryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(decryptResult).containsEntry(key, textToEncrypt);
    }

    @Test
    void shouldEncryptDecryptNestedMapData() {
        // given
        String key = "mapKey";
        List<String> propertyList = Collections.singletonList("mapKey.mapKey");
        String textToEncrypt = "some text";
        Map<String, Map<String, String>> data = new HashMap<>();
        Map<String, String> nestedData = new HashMap<>();

        nestedData.put(key, textToEncrypt);

        data.put(key, nestedData);

        // when
        Map<String, Map<String, String>> result = dataEncryptionService.encryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(result).isNotNull();
        assertThat(result.get(key)).doesNotContainEntry(key, textToEncrypt);

        // and when
        Map<String, Map<String, String>> decryptResult = dataEncryptionService.decryptData(data, propertyList, EncryptionContext.builder().build());

        // then
        assertThat(decryptResult).isNotNull();
        assertThat(result.get(key)).containsEntry(key, textToEncrypt);
    }
}
