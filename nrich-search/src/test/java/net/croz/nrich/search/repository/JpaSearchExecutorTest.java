package net.croz.nrich.search.repository;

import net.croz.nrich.search.SearchTestConfiguration;
import net.croz.nrich.search.api.model.SearchConfiguration;
import net.croz.nrich.search.api.model.SearchJoin;
import net.croz.nrich.search.api.model.property.SearchPropertyJoin;
import net.croz.nrich.search.api.model.SubqueryConfiguration;
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
@SpringJUnitConfig(SearchTestConfiguration.class)
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
        final Optional<TestEntity> result = testEntitySearchRepository.findOne(request, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldReturnEmptyOptionalWhenNoResultsHaveBeenFound() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = new TestEntitySearchRequest("non existing name");

        // when
        final Optional<TestEntity> result = testEntitySearchRepository.findOne(request, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindAll() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = new TestEntitySearchRequest("FIRst0");

        // when
        final List<TestEntity> results = testEntitySearchRepository.findAll(request, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(results).hasSize(1);
    }

    @Test
    void shouldFindAllWithSort() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = new TestEntitySearchRequest(null);

        // when
        final List<TestEntity> result = testEntitySearchRepository.findAll(request, SearchConfiguration.emptyConfiguration(), Sort.by(Sort.Order.desc("age")));

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getAge()).isEqualTo(28);
    }

    @Test
    void shouldFetchOnlySubsetOfResult() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = new TestEntitySearchRequest(null);

        // when
        final Page<TestEntity> result = testEntitySearchRepository.findAll(request, SearchConfiguration.emptyConfiguration(), PageRequest.of(0, 1));

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.getTotalPages()).isEqualTo(5);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void shouldReturnWholeResultListWhenRequestIsUnpaged() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = new TestEntitySearchRequest(null);

        // when
        final Page<TestEntity> result = testEntitySearchRepository.findAll(request, SearchConfiguration.emptyConfiguration(), Pageable.unpaged());

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(5);
    }

    @Test
    void shouldCount() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = new TestEntitySearchRequest("FIRst0");

        // when
        final long result = testEntitySearchRepository.count(request, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    void shouldReturnZeroWhenThereAreNoResults() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = new TestEntitySearchRequest("second non existing name");

        // when
        final long result = testEntitySearchRepository.count(request, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(result).isEqualTo(0L);
    }

    @Test
    void shouldCountDistinctEntities() {
        // given
        generateListForSearch(entityManager, 2);

        final TestEntitySearchRequest request = new TestEntitySearchRequest(null);

        final SearchJoin<TestEntitySearchRequest> collectionJoin = SearchJoin.<TestEntitySearchRequest>builder().alias("collectionEntityList").path("collectionEntityList").joinType(JoinType.LEFT).build();

        final SearchConfiguration<TestEntity, TestEntityDto, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntityDto, TestEntitySearchRequest>builder()
                .distinct(true)
                .joinList(Collections.singletonList(collectionJoin))
                .build();

        // when
        final long result = testEntitySearchRepository.count(request, searchConfiguration);

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

        final SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
                .subqueryConfigurationList(Collections.singletonList(subqueryConfiguration))
                .build();

        final TestEntitySearchRequest request = TestEntitySearchRequest.builder()
                .subqueryRestrictionName("first0-association-1")
                .build();

        // when
        final long result = testEntitySearchRepository.count(request, searchConfiguration);

        // then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    void shouldNotFailWhenThereIsNoContent() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = new TestEntitySearchRequest("non existing name");

        // when
        final Page<TestEntity> result = testEntitySearchRepository.findAll(request, SearchConfiguration.emptyConfiguration(), PageRequest.of(0, 1));

        // then
        assertThat(result).isEmpty();
        assertThat(result.getTotalPages()).isEqualTo(0);
        assertThat(result.getContent()).hasSize(0);
    }

    @Test
    void shouldReturnTrueWhenEntityExists() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = new TestEntitySearchRequest("first1");

        // when
        final boolean result = testEntitySearchRepository.exists(request, SearchConfiguration.emptyConfigurationWithDefaultMappingResolve());

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenEntityDoesntExist() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = new TestEntitySearchRequest("first non existing entity");

        // when
        final boolean result = testEntitySearchRepository.exists(request, SearchConfiguration.emptyConfigurationWithDefaultMappingResolve());

        // then
        assertThat(result).isFalse();
    }
}
