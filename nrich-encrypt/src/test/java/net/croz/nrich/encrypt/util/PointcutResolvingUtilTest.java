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

package net.croz.nrich.encrypt.util;

import net.croz.nrich.encrypt.api.model.EncryptionConfiguration;
import net.croz.nrich.encrypt.api.model.EncryptionOperation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PointcutResolvingUtilTest {

    @Test
    void shouldResolvePointcutFromEncryptionConfigurationList() {
        // given
        String method = "net.croz.nrich.encrypt.stub.ExampleService.exampleMethod";
        EncryptionConfiguration configuration = new EncryptionConfiguration(method, List.of("id"), EncryptionOperation.ENCRYPT);
        List<EncryptionConfiguration> encryptionConfigurationList = List.of(configuration);

        // when
        String result = PointcutResolvingUtil.resolvePointcutFromEncryptionConfigurationList(encryptionConfigurationList);

        // then
        assertThat(result).isEqualTo(String.format("execution(* %s(..))", method));
    }
}
