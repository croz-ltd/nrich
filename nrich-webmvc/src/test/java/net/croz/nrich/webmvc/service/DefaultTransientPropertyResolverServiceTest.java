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

package net.croz.nrich.webmvc.service;

import net.croz.nrich.webmvc.WebmvcTestConfiguration;
import net.croz.nrich.webmvc.service.stub.TransientPropertyResolverServiceImplTestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringJUnitWebConfig(WebmvcTestConfiguration.class)
class DefaultTransientPropertyResolverServiceTest {

    @Autowired
    private DefaultTransientPropertyResolverService transientPropertyResolverService;

    @Test
    void shouldResolveTransientPropertyList() {
        // given
        Class<?> type = TransientPropertyResolverServiceImplTestRequest.TransientPropertyResolverServiceImplTestInnerRequest.class;

        // when
        List<String> resultList = transientPropertyResolverService.resolveTransientPropertyList(type);

        // then
        assertThat(resultList).containsExactlyInAnyOrder("value", "anotherValue");
    }

    @Test
    void shouldNotFailWhenResolvingTransientPropertyListFromInteface() {
        // when
        Throwable thrown = catchThrowable(() -> transientPropertyResolverService.resolveTransientPropertyList(Map.class));

        // then
        assertThat(thrown).isNull();
    }
}
