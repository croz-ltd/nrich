package net.croz.nrich.search.repository.impl;

import net.croz.nrich.search.SearchConfigurationTestConfiguration;
import net.croz.nrich.search.model.SearchSpecification;
import net.croz.nrich.search.model.SearchJoin;
import net.croz.nrich.search.model.SearchPropertyJoin;
import net.croz.nrich.search.model.SubqueryConfiguration;
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
import javax.persistence.criteria.JoinType;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static net.croz.nrich.search.repository.testutil.JpaSearchRepositoryExecutorGeneratingUtil.generateListForSearch;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringJUnitConfig(classes = SearchConfigurationTestConfiguration.class)
public class JpaSearchExecutorTest {

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

        final TestEntitySearchRequest request = new TestEntitySearchRequest("first0");

        // when
        final Optional<TestEntity> result = testEntitySearchRepository.findOne(request, SearchSpecification.emptySpecification());

        // then
        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldReturnEmptyOptionalWhenNoResultsHaveBeenFound() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = new TestEntitySearchRequest("non existing name");

        // when
        final Optional<TestEntity> result = testEntitySearchRepository.findOne(request, SearchSpecification.emptySpecification());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindAll() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = new TestEntitySearchRequest("FIRst0");

        // when
        final List<TestEntity> results = testEntitySearchRepository.findAll(request, SearchSpecification.emptySpecification());

        // then
        assertThat(results).hasSize(1);
    }

    @Test
    void shouldFindAllWithSort() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = new TestEntitySearchRequest(null);

        // when
        final List<TestEntity> results = testEntitySearchRepository.findAll(request, SearchSpecification.emptySpecification(), Sort.by(Sort.Order.desc("age")));

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getAge()).isEqualTo(28);
    }

    @Test
    void shouldFetchOnlySubsetOfResult() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = new TestEntitySearchRequest(null);

        // when
        final Page<TestEntity> results = testEntitySearchRepository.findAll(request, SearchSpecification.emptySpecification(), PageRequest.of(0, 1));

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.getTotalPages()).isEqualTo(5);
        assertThat(results.getContent()).hasSize(1);
    }

    @Test
    void shouldReturnWholeResultListWhenRequestIsUnpaged() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = new TestEntitySearchRequest(null);

        // when
        final Page<TestEntity> results = testEntitySearchRepository.findAll(request, SearchSpecification.emptySpecification(), Pageable.unpaged());

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.getTotalPages()).isEqualTo(1);
        assertThat(results.getContent()).hasSize(5);
    }

    @Test
    void shouldCount() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = new TestEntitySearchRequest("FIRst0");

        // when
        final long result = testEntitySearchRepository.count(request, SearchSpecification.emptySpecification());

        // then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    void shouldReturnZeroWhenThereAreNoResults() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = new TestEntitySearchRequest("second non existing name");

        // when
        final long result = testEntitySearchRepository.count(request, SearchSpecification.emptySpecification());

        // then
        assertThat(result).isEqualTo(0L);
    }

    @Test
    void shouldCountDistinctEntities() {
        // given
        generateListForSearch(entityManager, 2);

        final TestEntitySearchRequest request = new TestEntitySearchRequest(null);

        final SearchJoin<TestEntitySearchRequest> collectionJoin = SearchJoin.<TestEntitySearchRequest>builder().alias("collectionEntityList").path("collectionEntityList").joinType(JoinType.LEFT).build();

        final SearchSpecification<TestEntity, TestEntityDto, TestEntitySearchRequest> searchSpecification = SearchSpecification.<TestEntity, TestEntityDto, TestEntitySearchRequest>builder()
                .distinct(true)
                .joinList(Collections.singletonList(collectionJoin))
                .build();

        // when
        final long result = testEntitySearchRepository.count(request, searchSpecification);

        // then
        assertThat(result).isEqualTo(5L);
    }

    @Test
    void shouldCountWhenUsingSearchingSubEntity() {
        // given
        generateListForSearch(entityManager);

        final SubqueryConfiguration subqueryConfiguration = SubqueryConfiguration.builder()
                .rootEntity(TestEntityCollectionWithReverseAssociation.class)
                .propertyPrefix("subqueryRestriction")
                .joinBy(new SearchPropertyJoin("id", "testEntity.id")).build();

        final SearchSpecification<TestEntity, TestEntity, TestEntitySearchRequest> searchSpecification = SearchSpecification.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
                .subqueryConfigurationList(Collections.singletonList(subqueryConfiguration))
                .build();

        final TestEntitySearchRequest request = TestEntitySearchRequest.builder()
                .subqueryRestrictionName("first0-association-1")
                .build();

        // when
        final long result = testEntitySearchRepository.count(request, searchSpecification);

        // then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    void shouldNotFailWhenThereIsNoContent() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = new TestEntitySearchRequest("non existing name");

        // when
        final Page<TestEntity> results = testEntitySearchRepository.findAll(request, SearchSpecification.emptySpecification(), PageRequest.of(0, 1));

        // then
        assertThat(results).isEmpty();
        assertThat(results.getTotalPages()).isEqualTo(0);
        assertThat(results.getContent()).hasSize(0);
    }

    @Test
    void shouldReturnTrueWhenEntityExists() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = new TestEntitySearchRequest("first1");

        // when
        final boolean result = testEntitySearchRepository.exists(request, SearchSpecification.emptySpecificationWithDefaultMappingResolve());

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenEntityDoesntExist() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = new TestEntitySearchRequest("first non existing entity");

        // when
        final boolean result = testEntitySearchRepository.exists(request, SearchSpecification.emptySpecificationWithDefaultMappingResolve());

        // then
        assertThat(result).isFalse();
    }
}
