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

package net.croz.nrich.encrypt.aspect;

import net.croz.nrich.encrypt.EncryptTestConfiguration;
import net.croz.nrich.encrypt.aspect.stub.EncryptDataAspectTestService;
import net.croz.nrich.encrypt.aspect.stub.EncryptDataAspectTestServiceResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(EncryptTestConfiguration.class)
class EncryptDataAspectTest {

    private static final String VALUE_TO_ENCRYPT = "some text";

    @Autowired
    private EncryptDataAspectTestService encryptDataAspectTestService;

    @Test
    void shouldEncryptAndDecryptData() {
        // when
        EncryptDataAspectTestServiceResult result = encryptDataAspectTestService.dataToEncrypt(VALUE_TO_ENCRYPT);

        // then
        assertThat(result.getValue()).isNotEqualTo(VALUE_TO_ENCRYPT);

        // and when
        EncryptDataAspectTestServiceResult decryptResult = encryptDataAspectTestService.dataToDecrypt(result);

        // then
        assertThat(decryptResult.getValue()).isEqualTo(VALUE_TO_ENCRYPT);
    }

    @Test
    void shouldNotEncryptNullData() {
        // when
        EncryptDataAspectTestServiceResult result = encryptDataAspectTestService.dataToEncrypt();

        // then
        assertThat(result).isNull();
    }

    @Test
    void shouldEncryptCompletableFutureData() throws Exception {
        // when
        EncryptDataAspectTestServiceResult result = encryptDataAspectTestService.dataToEncryptWithCompletableFuture(VALUE_TO_ENCRYPT).get();

        // then
        assertThat(result.getValue()).isNotEqualTo(VALUE_TO_ENCRYPT);

        // and when
        EncryptDataAspectTestServiceResult decryptResult = encryptDataAspectTestService.dataToDecrypt(result);

        // then
        assertThat(decryptResult.getValue()).isEqualTo(VALUE_TO_ENCRYPT);
    }

    @Test
    void shouldEncryptMonoData() {
        // when
        EncryptDataAspectTestServiceResult result = encryptDataAspectTestService.dataToEncryptWithMono(VALUE_TO_ENCRYPT).block();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getValue()).isNotEqualTo(VALUE_TO_ENCRYPT);

        // and when
        EncryptDataAspectTestServiceResult decryptResult = encryptDataAspectTestService.dataToDecrypt(result);

        // then
        assertThat(decryptResult.getValue()).isEqualTo(VALUE_TO_ENCRYPT);
    }

    @Test
    void shouldEncryptFluxData() {
        // when
        EncryptDataAspectTestServiceResult result = encryptDataAspectTestService.dataToEncryptWithFlux(VALUE_TO_ENCRYPT).blockFirst();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getValue()).isNotEqualTo(VALUE_TO_ENCRYPT);

        // and when
        EncryptDataAspectTestServiceResult decryptResult = encryptDataAspectTestService.dataToDecrypt(result);

        // then
        assertThat(decryptResult.getValue()).isEqualTo(VALUE_TO_ENCRYPT);
    }

    @Test
    void shouldNotEncryptUnsupportedReactorClass() {
        // when
        EncryptDataAspectTestServiceResult result = encryptDataAspectTestService.dataToEncryptWithUnsupportedReactorClass(VALUE_TO_ENCRYPT).get();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getValue()).isEqualTo(VALUE_TO_ENCRYPT);
    }

    @Test
    void shouldEncryptText() {
        // when
        String result = encryptDataAspectTestService.textToEncrypt(VALUE_TO_ENCRYPT);

        // then
        assertThat(result).isNotEqualTo(VALUE_TO_ENCRYPT);

        // and when
        String decryptResult = encryptDataAspectTestService.textToDecrypt(result, "ignored value");

        // then
        assertThat(decryptResult).isEqualTo(VALUE_TO_ENCRYPT);
    }
}
