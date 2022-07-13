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

package net.croz.nrich.search.factory;

import net.croz.nrich.search.factory.stub.SearchGetBeanConfiguration;
import net.croz.nrich.search.SearchTestConfiguration;
import net.croz.nrich.search.repository.stub.TestEntitySearchRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = { SearchGetBeanConfiguration.class, SearchTestConfiguration.class })
class SearchExecutorJpaRepositoryFactoryBeanIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void shouldCreateRepositoryWhenThereAreGetBeanCallsInConfiguration() {
        // when
        TestEntitySearchRepository result = applicationContext.getBean(TestEntitySearchRepository.class);

        // then
        assertThat(result).isNotNull();
    }
}
