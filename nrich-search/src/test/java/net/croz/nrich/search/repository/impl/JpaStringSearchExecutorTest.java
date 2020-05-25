package net.croz.nrich.search.repository.impl;

import net.croz.nrich.search.SearchConfigurationTestConfiguration;
import net.croz.nrich.search.model.SearchConfiguration;
import net.croz.nrich.search.repository.stub.TestEntityStringSearchRepository;
import net.croz.nrich.search.repository.stub.TestStringSearchEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static net.croz.nrich.search.repository.testutil.JpaSearchRepositoryExecutorGeneratingUtil.generateListForStringSearch;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringJUnitConfig(classes = SearchConfigurationTestConfiguration.class)
public class JpaStringSearchExecutorTest {

    @Autowired
    private TestEntityStringSearchRepository testEntityStringSearchRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldFindOne() {
        // given
        generateListForStringSearch(entityManager);

        // when
        final Optional<TestStringSearchEntity> result = testEntityStringSearchRepository.findOne("01.01.1970", Collections.singletonList("localDate"), SearchConfiguration.emptyConfiguration());

        // then
        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldFindOneMatchingAnyProperty() {
        // given
        generateListForStringSearch(entityManager);

        // when
        final Optional<TestStringSearchEntity> result = testEntityStringSearchRepository.findOne("01.01.1970", Arrays.asList("age", "localDate", "name"), SearchConfiguration.emptyConfigurationMatchingAny());

        // then
        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldFindAll() {
        // given
        generateListForStringSearch(entityManager);

        // when
        final List<TestStringSearchEntity> result = testEntityStringSearchRepository.findAll("name 1", Collections.singletonList("name"), SearchConfiguration.emptyConfiguration());

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    void shouldFindAllWithSort() {
        // given
        generateListForStringSearch(entityManager);

        // when
        final List<TestStringSearchEntity> result = testEntityStringSearchRepository.findAll("name", Collections.singletonList("name"), SearchConfiguration.emptyConfiguration(), Sort.by(Sort.Order.desc("name")));

        // then
        assertThat(result).hasSize(5);
        assertThat(result.get(0).getName()).isEqualTo("name 4");
    }

    @Test
    void shouldFindAllWithPaging() {
        // given
        generateListForStringSearch(entityManager);

        // when
        final Page<TestStringSearchEntity> result = testEntityStringSearchRepository.findAll("51", Collections.singletonList("age"), SearchConfiguration.emptyConfiguration(), PageRequest.of(0, 1));

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    void shouldCount() {
        // given
        generateListForStringSearch(entityManager);

        // when
        final long result = testEntityStringSearchRepository.count("51", Collections.singletonList("age"), SearchConfiguration.emptyConfiguration());

        // then
        assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldReturnTrueWhenEntityExists() {
        // given
        generateListForStringSearch(entityManager);

        // when
        final boolean result = testEntityStringSearchRepository.exists("51", Collections.singletonList("age"), SearchConfiguration.emptyConfiguration());

        // then
        assertThat(result).isTrue();
    }
}
