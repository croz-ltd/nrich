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

package net.croz.nrich.registry.enumadata.service;

import net.croz.nrich.registry.RegistryTestConfiguration;
import net.croz.nrich.registry.api.enumdata.model.EnumResult;
import net.croz.nrich.registry.api.enumdata.request.ListBulkRegistryEnumRequest;
import net.croz.nrich.registry.api.enumdata.request.ListRegistryEnumRequest;
import net.croz.nrich.registry.api.enumdata.service.RegistryEnumService;
import net.croz.nrich.registry.enumadata.stub.RegistryTestEnum;
import net.croz.nrich.registry.enumadata.stub.RegistryTestEnumWithAdditionalData;
import net.croz.nrich.registry.enumadata.stub.RegistryTestEnumWithoutDescription;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.util.List;
import java.util.Map;

import static net.croz.nrich.registry.enumadata.testutil.RegistryEnumGeneratingUtil.createBulkListEnumRequest;
import static net.croz.nrich.registry.enumadata.testutil.RegistryEnumGeneratingUtil.createListEnumRequest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitWebConfig(RegistryTestConfiguration.class)
class DefaultRegistryEnumServiceTest {

    @Autowired
    private RegistryEnumService registryEnumService;

    @Test
    void shouldBulkList() {
        // given
        String classFullName = RegistryTestEnum.class.getName();
        ListBulkRegistryEnumRequest request = createBulkListEnumRequest(classFullName, "First");

        // when
        Map<String, List<EnumResult>> result = registryEnumService.listBulk(request);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(classFullName)).hasSize(1);

        EnumResult enumResult = result.get(classFullName).get(0);

        assertThat(enumResult.getId()).isEqualTo(classFullName);
        assertThat(enumResult.getDescription()).isEqualTo("First description");
        assertThat(enumResult.getValue()).isEqualTo(RegistryTestEnum.FIRST);
        assertThat(enumResult.getAdditionalData()).isEmpty();
    }

    @Test
    void shouldList() {
        // given
        String classFullName = RegistryTestEnumWithAdditionalData.class.getName();
        ListRegistryEnumRequest request = createListEnumRequest(classFullName, "Second");

        // when
        List<EnumResult> result = registryEnumService.list(request);

        // then
        assertThat(result).hasSize(1);

        EnumResult enumResult = result.get(0);

        assertThat(enumResult.getDescription()).isEqualTo("Second with additional methods description");
        assertThat(enumResult.getValue()).isEqualTo(RegistryTestEnumWithAdditionalData.SECOND_WITH_ADDITIONAL_METHODS);
        assertThat(enumResult.getAdditionalData()).isEqualTo(Map.of("firstAdditionalMethod", List.of("additional method second value")));
    }

    @Test
    void shouldReturnEmptyListForNonExistingEnum() {
        // given
        String classFullName = "net.croz.nrich.registry.NonExistingEnum";
        ListRegistryEnumRequest request = createListEnumRequest(classFullName, "");

        // when
        List<EnumResult> result = registryEnumService.list(request);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyListForClassThatIsNotEnum() {
        // given
        String classFullName = Object.class.getName();
        ListRegistryEnumRequest request = createListEnumRequest(classFullName, "");

        // when
        List<EnumResult> result = registryEnumService.list(request);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEnumNameWhenDescriptionIsNotDefined() {
        // given
        String classFullName = RegistryTestEnumWithoutDescription.class.getName();
        ListRegistryEnumRequest request = createListEnumRequest(classFullName, "");

        // when
        List<EnumResult> result = registryEnumService.list(request);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDescription()).isEqualTo("FIRST");
    }
}
