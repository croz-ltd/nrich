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

package net.croz.nrich.search.repository;

import net.croz.nrich.search.SearchTestConfiguration;
import net.croz.nrich.search.repository.stub.TestEntityWithNaturalId;
import net.croz.nrich.search.repository.stub.TestEntityWithNaturalIdSearchRepository;
import net.croz.nrich.search.repository.stub.TestEntityWithSimpleNaturalId;
import net.croz.nrich.search.repository.stub.TestEntityWithSimpleNaturalIdSearchRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Map;
import java.util.Optional;

import static net.croz.nrich.search.repository.testutil.HibernateNaturalIdSearchExecutorGeneratingUtil.createNaturalId;
import static net.croz.nrich.search.repository.testutil.HibernateNaturalIdSearchExecutorGeneratingUtil.generateListForNaturalIdSearch;
import static net.croz.nrich.search.repository.testutil.HibernateNaturalIdSearchExecutorGeneratingUtil.generateListForSimpleNaturalIdSearch;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringJUnitConfig(SearchTestConfiguration.class)
class HibernateNaturalIdSearchExecutorTest {

    @Autowired
    private TestEntityWithSimpleNaturalIdSearchRepository testEntityWithSimpleNaturalIdSearchRepository;

    @Autowired
    private TestEntityWithNaturalIdSearchRepository testEntityWithNaturalIdSearchRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldFindBySimpleNaturalId() {
        // given
        generateListForSimpleNaturalIdSearch(entityManager);
        String naturalId = "simple 1";

        // when
        Optional<TestEntityWithSimpleNaturalId> result = testEntityWithSimpleNaturalIdSearchRepository.findBySimpleNaturalId(naturalId);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.get().getNaturalId()).isEqualTo(naturalId);

        // and when
        Optional<TestEntityWithSimpleNaturalId> nonExistingResult = testEntityWithSimpleNaturalIdSearchRepository.findBySimpleNaturalId("non existing");

        // then
        assertThat(nonExistingResult).isEmpty();
    }

    @Test
    void shouldFindByNaturalId() {
        // given
        generateListForNaturalIdSearch(entityManager);
        Map<String, Object> naturalId = createNaturalId("first 1", "second 1");

        // when
        Optional<TestEntityWithNaturalId> result = testEntityWithNaturalIdSearchRepository.findByNaturalId(naturalId);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.get().getFirstNaturalId()).isEqualTo(naturalId.get("firstNaturalId"));
        assertThat(result.get().getSecondNaturalId()).isEqualTo(naturalId.get("secondNaturalId"));

        // and when
        Optional<TestEntityWithNaturalId> nonExistingResult = testEntityWithNaturalIdSearchRepository.findByNaturalId(createNaturalId("first 1", "second 2"));

        // then
        assertThat(nonExistingResult).isEmpty();
    }
}
