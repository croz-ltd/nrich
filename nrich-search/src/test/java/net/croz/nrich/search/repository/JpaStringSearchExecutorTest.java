package net.croz.nrich.search.repository;

import net.croz.nrich.search.SearchTestConfiguration;
import net.croz.nrich.search.api.model.SearchConfiguration;
import net.croz.nrich.search.repository.stub.TestEntityStringSearchRepository;
import net.croz.nrich.search.repository.stub.TestStringSearchEntity;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static net.croz.nrich.search.repository.testutil.JpaSearchRepositoryExecutorGeneratingUtil.generateListForStringSearch;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringJUnitConfig(SearchTestConfiguration.class)
class JpaStringSearchExecutorTest {

    private static final SearchConfiguration<TestStringSearchEntity, TestStringSearchEntity, Map<String, Object>> EMPTY_CONFIGURATION = SearchConfiguration.emptyConfiguration();

    @Autowired
    private TestEntityStringSearchRepository testEntityStringSearchRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldFindOne() {
        // given
        generateListForStringSearch(entityManager);

        // when
        Optional<TestStringSearchEntity> result = testEntityStringSearchRepository.findOne("01.01.1970.", Collections.singletonList("localDate"), EMPTY_CONFIGURATION);

        // then
        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldReturnEmptyOptionalWhenNoResultsHaveBeenFound() {
        // given
        generateListForStringSearch(entityManager);

        // when
        Optional<TestStringSearchEntity> result = testEntityStringSearchRepository.findOne("01.01.2000.", Collections.singletonList("localDate"), EMPTY_CONFIGURATION);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindOneMatchingAnyProperty() {
        // given
        generateListForStringSearch(entityManager);

        SearchConfiguration<TestStringSearchEntity, TestStringSearchEntity, Map<String, Object>> searchConfiguration = SearchConfiguration.emptyConfigurationMatchingAny();

        // when
        Optional<TestStringSearchEntity> result = testEntityStringSearchRepository.findOne("01.01.1970.", Arrays.asList("age", "localDate", "name"), searchConfiguration);

        // then
        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldFindAll() {
        // given
        generateListForStringSearch(entityManager);

        // when
        List<TestStringSearchEntity> result = testEntityStringSearchRepository.findAll("name 1", Collections.singletonList("name"), EMPTY_CONFIGURATION);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    void shouldFindAllWithSort() {
        // given
        generateListForStringSearch(entityManager);

        // when
        List<TestStringSearchEntity> result = testEntityStringSearchRepository.findAll("name", Collections.singletonList("name"), EMPTY_CONFIGURATION, Sort.by(Sort.Order.desc("name")));

        // then
        assertThat(result).hasSize(5);
        assertThat(result.get(0).getName()).isEqualTo("name 4");
    }

    @Test
    void shouldFindAllWithPaging() {
        // given
        generateListForStringSearch(entityManager);

        // when
        Page<TestStringSearchEntity> result = testEntityStringSearchRepository.findAll("51", Collections.singletonList("age"), EMPTY_CONFIGURATION, PageRequest.of(0, 1));

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    void shouldReturnWholeResultListWhenRequestIsUnpaged() {
        // given
        generateListForStringSearch(entityManager);

        // when
        Page<TestStringSearchEntity> result = testEntityStringSearchRepository.findAll("10", Collections.singletonList("ageFrom"), EMPTY_CONFIGURATION, Pageable.unpaged());

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(5);
    }

    @Test
    void shouldCount() {
        // given
        generateListForStringSearch(entityManager);

        // when
        long result = testEntityStringSearchRepository.count("51", Collections.singletonList("age"), EMPTY_CONFIGURATION);

        // then
        assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldReturnZeroWhenThereAreNoResults() {
        // given
        generateListForStringSearch(entityManager);

        // when
        long result = testEntityStringSearchRepository.count("5555", Collections.singletonList("age"), EMPTY_CONFIGURATION);

        // then
        assertThat(result).isZero();
    }

    @Test
    void shouldReturnTrueWhenEntityExists() {
        // given
        generateListForStringSearch(entityManager);

        // when
        boolean result = testEntityStringSearchRepository.exists("51", Collections.singletonList("age"), EMPTY_CONFIGURATION);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenEntityDoesntExist() {
        // given
        generateListForStringSearch(entityManager);

        // when
        boolean result = testEntityStringSearchRepository.exists("51111", Collections.singletonList("age"), EMPTY_CONFIGURATION);

        // then
        assertThat(result).isFalse();
    }
}
