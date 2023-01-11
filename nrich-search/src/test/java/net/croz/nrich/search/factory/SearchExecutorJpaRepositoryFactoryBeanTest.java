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

package net.croz.nrich.search.factory;

import net.croz.nrich.search.SearchTestConfiguration;
import net.croz.nrich.search.factory.stub.SearchExecutorJpaRepositoryFactoryBeanTestRepository;
import net.croz.nrich.search.factory.stub.SearchExecutorJpaRepositoryFactoryBeanTestSearchExecutor;
import net.croz.nrich.search.factory.stub.SearchExecutorJpaRepositoryFactoryBeanTestStringAndSearchExecutor;
import net.croz.nrich.search.factory.stub.SearchExecutorJpaRepositoryFactoryBeanTestStringSearchExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(SearchTestConfiguration.class)
class SearchExecutorJpaRepositoryFactoryBeanTest {

    @Autowired
    private SearchExecutorJpaRepositoryFactoryBeanTestRepository searchExecutorJpaRepositoryFactoryBeanTestRepository;

    @Autowired
    private SearchExecutorJpaRepositoryFactoryBeanTestSearchExecutor searchExecutorJpaRepositoryFactoryBeanTestSearchExecutor;

    @Autowired
    private SearchExecutorJpaRepositoryFactoryBeanTestStringSearchExecutor searchExecutorJpaRepositoryFactoryBeanTestStringSearchExecutor;

    @Autowired
    private SearchExecutorJpaRepositoryFactoryBeanTestStringAndSearchExecutor searchExecutorJpaRepositoryFactoryBeanTestStringAndSearchExecutor;

    @Test
    void shouldInitializeRegularRepositoryBean() {
        // expect
        assertThat(searchExecutorJpaRepositoryFactoryBeanTestRepository).isNotNull();
    }

    @Test
    void shouldInitializeSearchExecutorRepository() {
        // expect
        assertThat(searchExecutorJpaRepositoryFactoryBeanTestSearchExecutor).isNotNull();
    }

    @Test
    void shouldInitializeStringSearchExecutorRepository() {
        // expect
        assertThat(searchExecutorJpaRepositoryFactoryBeanTestStringSearchExecutor).isNotNull();
    }

    @Test
    void shouldInitializeBeanThatImplementsBothInterfacesCorrectly() {
        // expect
        assertThat(searchExecutorJpaRepositoryFactoryBeanTestStringAndSearchExecutor).isNotNull();
    }
}
