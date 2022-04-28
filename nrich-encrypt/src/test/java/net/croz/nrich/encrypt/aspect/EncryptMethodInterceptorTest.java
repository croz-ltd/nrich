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

package net.croz.nrich.encrypt.aspect;

import net.croz.nrich.encrypt.EncryptTestConfiguration;
import net.croz.nrich.encrypt.aspect.stub.EncryptDataAspectTestService;
import net.croz.nrich.encrypt.aspect.stub.EncryptDataAspectTestServiceResult;
import net.croz.nrich.encrypt.aspect.stub.EncryptionMethodInterceptorTestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(EncryptTestConfiguration.class)
class EncryptMethodInterceptorTest {

    @Autowired
    private EncryptDataAspectTestService encryptDataAspectTestService;

    @Autowired
    private EncryptionMethodInterceptorTestService encryptionMethodInterceptorTestService;

    @Test
    void shouldEncryptDecryptData() {
        // given
        String text = "some text";

        // when
        EncryptDataAspectTestServiceResult result = encryptDataAspectTestService.dataToEncryptFromConfiguration(text);

        // then
        assertThat(result.getValue()).isNotEqualTo(text);

        // and when
        EncryptDataAspectTestServiceResult decryptResult = encryptDataAspectTestService.dataToDecryptFromConfiguration(result);

        // then
        assertThat(decryptResult.getValue()).isEqualTo(text);
    }

    @Test
    void shouldEncryptDecryptDataWithPointcutMatchingAllMethods() {
        // given
        String text = "some text";

        // when
        EncryptDataAspectTestServiceResult result = encryptionMethodInterceptorTestService.dataToEncryptFromConfiguration(text);

        // then
        assertThat(result.getValue()).isNotEqualTo(text);

        // and when
        String decryptResult = encryptionMethodInterceptorTestService.dataToDecryptFromConfiguration(result);

        // then
        assertThat(decryptResult).isEqualTo(text);
    }

    @Test
    void shouldNotEncryptDataForIgnoredMethod() {
        // given
        String text = "some text";

        // when
        EncryptDataAspectTestServiceResult result = encryptionMethodInterceptorTestService.ignoredMethod(text);

        // then
        assertThat(result.getValue()).isEqualTo(text);
    }

    @Test
    void shouldSkipResultEncryptionWhenAnnotationArgumentListIsEmpty() {
        // given
        String text = "some text";

        // when
        EncryptDataAspectTestServiceResult result = encryptDataAspectTestService.dataToEncryptWithInvalidAnnotation(text);

        // then
        assertThat(result.getValue()).isEqualTo(text);
    }

    @Test
    void shouldSkipDecryptionWhenAnnotationArgumentListIsEmpty() {
        // given
        String text = "some text";

        // when
        EncryptDataAspectTestServiceResult result = encryptDataAspectTestService.dataToEncrypt(text);

        // then
        assertThat(result.getValue()).isNotEqualTo(text);

        // and when
        EncryptDataAspectTestServiceResult decryptResult = encryptDataAspectTestService.dataToDecryptWithInvalidAnnotation(result);

        // then
        assertThat(decryptResult.getValue()).isEqualTo(result.getValue());
    }
}
