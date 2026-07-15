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
import net.croz.nrich.registry.api.enumdata.request.ListRegistryEnumRequest;
import net.croz.nrich.registry.api.enumdata.service.RegistryEnumService;
import net.croz.nrich.registry.enumadata.stub.RegistryTestEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.time.DayOfWeek;
import java.util.List;

import static net.croz.nrich.registry.enumadata.testutil.RegistryEnumGeneratingUtil.createListEnumRequest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitWebConfig(RegistryTestConfiguration.class)
@TestPropertySource(properties = "nrich.registry.allowed-enum-package-list=net.croz.nrich.registry.enumadata.stub")
class DefaultRegistryEnumServiceWithAllowedPackageListTest {

    @Autowired
    private RegistryEnumService registryEnumService;

    @Test
    void shouldAcceptEnumFromAllowedPackage() {
        // given
        ListRegistryEnumRequest request = createListEnumRequest(RegistryTestEnum.class.getName(), "");

        // when
        List<EnumResult> result = registryEnumService.list(request);

        // then
        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldRejectEnumOutsideAllowedPackage() {
        // given
        ListRegistryEnumRequest request = createListEnumRequest(DayOfWeek.class.getName(), "");

        // when
        List<EnumResult> result = registryEnumService.list(request);

        // then
        assertThat(result).isEmpty();
    }
}
