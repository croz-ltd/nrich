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
import net.croz.nrich.search.api.model.SearchConfiguration;
import net.croz.nrich.search.api.model.SearchJoin;
import net.croz.nrich.search.api.model.property.SearchPropertyJoin;
import net.croz.nrich.search.api.model.subquery.SubqueryConfiguration;
import net.croz.nrich.search.repository.stub.TestEntity;
import net.croz.nrich.search.repository.stub.TestEntityCollectionWithReverseAssociation;
import net.croz.nrich.search.repository.stub.TestEntityDto;
import net.croz.nrich.search.repository.stub.TestEntitySearchRepository;
import net.croz.nrich.search.repository.stub.TestEntitySearchRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static net.croz.nrich.search.repository.testutil.JpaSearchRepositoryExecutorGeneratingUtil.generateListForSearch;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringJUnitConfig(SearchTestConfiguration.class)
class JpaSearchExecutorTest {

    @Autowired
    private TestEntitySearchRepository testEntitySearchRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldInjectRepository() {
        assertThat(testEntitySearchRepository).isNotNull();
    }

    @Test
    void shouldFindOne() {
        // given
        generateListForSearch(entityManager);

        TestEntitySearchRequest request = new TestEntitySearchRequest("first0");

        // when
        Optional<TestEntity> result = testEntitySearchRepository.findOne(request, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldReturnEmptyOptionalWhenNoResultsHaveBeenFound() {
        // given
        generateListForSearch(entityManager);

        TestEntitySearchRequest request = new TestEntitySearchRequest("non existing name");

        // when
        Optional<TestEntity> result = testEntitySearchRepository.findOne(request, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindAll() {
        // given
        generateListForSearch(entityManager);

        TestEntitySearchRequest request = new TestEntitySearchRequest("FIRst0");

        // when
        List<TestEntity> results = testEntitySearchRepository.findAll(request, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(results).hasSize(1);
    }

    @Test
    void shouldFindAllWithSort() {
        // given
        generateListForSearch(entityManager);

        TestEntitySearchRequest request = new TestEntitySearchRequest(null);

        // when
        List<TestEntity> result = testEntitySearchRepository.findAll(request, SearchConfiguration.emptyConfiguration(), Sort.by(Sort.Order.desc("age")));

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getAge()).isEqualTo(28);
    }

    @Test
    void shouldFetchOnlySubsetOfResult() {
        // given
        generateListForSearch(entityManager);

        TestEntitySearchRequest request = new TestEntitySearchRequest(null);

        // when
        Page<TestEntity> result = testEntitySearchRepository.findAll(request, SearchConfiguration.emptyConfiguration(), PageRequest.of(0, 1));

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.getTotalPages()).isEqualTo(5);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void shouldReturnWholeResultListWhenRequestIsUnpaged() {
        // given
        generateListForSearch(entityManager);

        TestEntitySearchRequest request = new TestEntitySearchRequest(null);

        // when
        Page<TestEntity> result = testEntitySearchRepository.findAll(request, SearchConfiguration.emptyConfiguration(), Pageable.unpaged());

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(5);
    }

    @Test
    void shouldCount() {
        // given
        generateListForSearch(entityManager);

        TestEntitySearchRequest request = new TestEntitySearchRequest("FIRst0");

        // when
        long result = testEntitySearchRepository.count(request, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    void shouldReturnZeroWhenThereAreNoResults() {
        // given
        generateListForSearch(entityManager);

        TestEntitySearchRequest request = new TestEntitySearchRequest("second non existing name");

        // when
        long result = testEntitySearchRepository.count(request, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(result).isZero();
    }

    @Test
    void shouldCountDistinctEntities() {
        // given
        generateListForSearch(entityManager, 2);

        TestEntitySearchRequest request = new TestEntitySearchRequest(null);

        SearchConfiguration<TestEntity, TestEntityDto, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntityDto, TestEntitySearchRequest>builder()
            .distinct(true)
            .joinList(Collections.singletonList(SearchJoin.leftJoin("collectionEntityList")))
            .build();

        // when
        long result = testEntitySearchRepository.count(request, searchConfiguration);

        // then
        assertThat(result).isEqualTo(5L);
    }

    @Test
    void shouldCountDistinctEntitiesWithJoinFetch() {
        // given
        generateListForSearch(entityManager, 2);

        TestEntitySearchRequest request = new TestEntitySearchRequest(null);

        SearchConfiguration<TestEntity, TestEntityDto, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntityDto, TestEntitySearchRequest>builder()
            .distinct(true)
            .joinList(Collections.singletonList(SearchJoin.innerJoinFetch("collectionEntityList")))
            .build();
        // when
        long result = testEntitySearchRepository.count(request, searchConfiguration);

        // then
        assertThat(result).isEqualTo(5L);
    }

    @Test
    void shouldCountWhenUsingSearchingSubEntity() {
        // given
        generateListForSearch(entityManager);

        SubqueryConfiguration subqueryConfiguration = SubqueryConfiguration.builder()
            .rootEntity(TestEntityCollectionWithReverseAssociation.class)
            .propertyPrefix("subqueryRestriction")
            .joinBy(new SearchPropertyJoin("id", "testEntity.id")).build();

        SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
            .subqueryConfigurationList(Collections.singletonList(subqueryConfiguration))
            .build();

        TestEntitySearchRequest request = TestEntitySearchRequest.builder()
            .subqueryRestrictionName("first0-association-1")
            .build();

        // when
        long result = testEntitySearchRepository.count(request, searchConfiguration);

        // then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    void shouldNotFailWhenThereIsNoContent() {
        // given
        generateListForSearch(entityManager);

        TestEntitySearchRequest request = new TestEntitySearchRequest("non existing name");

        // when
        Page<TestEntity> result = testEntitySearchRepository.findAll(request, SearchConfiguration.emptyConfiguration(), PageRequest.of(0, 1));

        // then
        assertThat(result).isEmpty();
        assertThat(result.getTotalPages()).isZero();
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void shouldReturnTrueWhenEntityExists() {
        // given
        generateListForSearch(entityManager);

        TestEntitySearchRequest request = new TestEntitySearchRequest("first1");

        // when
        boolean result = testEntitySearchRepository.exists(request, SearchConfiguration.emptyConfigurationWithDefaultMappingResolve());

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenEntityDoesntExist() {
        // given
        generateListForSearch(entityManager);

        TestEntitySearchRequest request = new TestEntitySearchRequest("first non existing entity");

        // when
        boolean result = testEntitySearchRepository.exists(request, SearchConfiguration.emptyConfigurationWithDefaultMappingResolve());

        // then
        assertThat(result).isFalse();
    }
}
