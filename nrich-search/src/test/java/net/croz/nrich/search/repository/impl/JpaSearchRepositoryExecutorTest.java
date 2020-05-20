package net.croz.nrich.search.repository.impl;

import net.croz.nrich.search.SearchConfigurationTestConfiguration;
import net.croz.nrich.search.model.DefaultRootEntityResolver;
import net.croz.nrich.search.model.PluralAssociationRestrictionType;
import net.croz.nrich.search.model.SearchConfiguration;
import net.croz.nrich.search.model.SearchJoin;
import net.croz.nrich.search.model.SearchProjection;
import net.croz.nrich.search.model.SearchPropertyJoin;
import net.croz.nrich.search.model.SearchPropertyMapping;
import net.croz.nrich.search.model.SubqueryConfiguration;
import net.croz.nrich.search.repository.stub.TestEntity;
import net.croz.nrich.search.repository.stub.TestEntityCollectionWithReverseAssociation;
import net.croz.nrich.search.repository.stub.TestEntityDto;
import net.croz.nrich.search.repository.stub.TestEntityEnum;
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
import javax.persistence.Tuple;
import javax.persistence.criteria.JoinType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static net.croz.nrich.search.repository.testutil.SearchRepositoryGeneratingUtil.generateListForSearch;
import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = SearchConfigurationTestConfiguration.class)
@Transactional
public class JpaSearchRepositoryExecutorTest {

    @Autowired
    private TestEntitySearchRepository testEntitySearchRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldInjectRepository() {
        assertThat(testEntitySearchRepository).isNotNull();
    }

    @Test
    void shouldSearchByRootEntityStringAttributes() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = new TestEntitySearchRequest("FIRst0");

        // when
        final List<TestEntity> results = testEntitySearchRepository.findAll(request, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.size()).isEqualTo(1);
    }

    @Test
    void shouldSearchByRootEntityNumberRangeIncluding() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = TestEntitySearchRequest.builder().ageFromIncluding(20).ageToIncluding(25).build();

        // when
        final List<TestEntity> results = testEntitySearchRepository.findAll(request, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.size()).isEqualTo(2);
    }

    @Test
    void shouldSearchByRootEntityNumberRange() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = TestEntitySearchRequest.builder().ageFrom(20).ageTo(25).build();

        // when
        final List<TestEntity> results = testEntitySearchRepository.findAll(request, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.size()).isEqualTo(1);
    }

    @Test
    void shouldSearchBySimpleAssociationValues() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = TestEntitySearchRequest.builder()
                .nestedEntity(new TestEntitySearchRequest.TestNestedEntitySearchRequest("nested0"))
                .build();

        // when
        final List<TestEntity> results = testEntitySearchRepository.findAll(request, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.size()).isEqualTo(1);
    }

    @Test
    void shouldSearchByCollectionAssociationValuesUsingExistsSubquery() {
        // given
        generateListForSearch(entityManager);

        final SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
                .pluralAssociationRestrictionType(PluralAssociationRestrictionType.EXISTS)
                .build();

        final TestEntitySearchRequest request = TestEntitySearchRequest.builder()
                .collectionEntityList(new TestEntitySearchRequest.TestCollectionEntitySearchRequest("collection2"))
                .build();

        // when
        final List<TestEntity> results = testEntitySearchRepository.findAll(request, searchConfiguration);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).getCollectionEntityList()).isNotEmpty();
        assertThat(results.get(0).getCollectionEntityList().get(0).getName()).isEqualTo("collection2");
    }

    @Test
    void shouldSearchByCollectionAssociationValuesUsingJoin() {
        // given
        generateListForSearch(entityManager);

        final SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
                .pluralAssociationRestrictionType(PluralAssociationRestrictionType.JOIN)
                .build();

        final TestEntitySearchRequest request = TestEntitySearchRequest.builder()
                .collectionEntityList(new TestEntitySearchRequest.TestCollectionEntitySearchRequest("collection2"))
                .build();

        // when
        final List<TestEntity> results = testEntitySearchRepository.findAll(request, searchConfiguration);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).getCollectionEntityList()).isNotEmpty();
        assertThat(results.get(0).getCollectionEntityList().get(0).getName()).isEqualTo("collection2");
    }

    @Test
    void shouldSearchByCollectionAssociationValuesByUsingFieldMapping() {
        // given
        generateListForSearch(entityManager);

        final SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
                .pluralAssociationRestrictionType(PluralAssociationRestrictionType.JOIN)
                .propertyMappingList(Collections.singletonList(new SearchPropertyMapping("collectionName", "collectionEntityList.name")))
                .build();

        final TestEntitySearchRequest request = TestEntitySearchRequest.builder()
                .collectionName("collection2")
                .build();

        // when
        final List<TestEntity> results = testEntitySearchRepository.findAll(request, searchConfiguration);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).getCollectionEntityList()).isNotEmpty();
        assertThat(results.get(0).getCollectionEntityList().get(0).getName()).isEqualTo("collection2");
    }

    @Test
    void shouldSearchByCollectionAssociationValuesByUsingFieldPrefix() {
        // given
        generateListForSearch(entityManager);

        final SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
                .pluralAssociationRestrictionType(PluralAssociationRestrictionType.JOIN)
                .resolveFieldMappingUsingPrefix(true)
                .build();

        final TestEntitySearchRequest request = TestEntitySearchRequest.builder()
                .collectionEntityListName("collection2")
                .build();

        // when
        final List<TestEntity> results = testEntitySearchRepository.findAll(request, searchConfiguration);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).getCollectionEntityList()).isNotEmpty();
        assertThat(results.get(0).getCollectionEntityList().get(0).getName()).isEqualTo("collection2");
    }

    @Test
    void shouldSearchEntitiesWithoutAssociationByUsingSubquery() {
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
        final List<TestEntity> results = testEntitySearchRepository.findAll(request, searchConfiguration);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.size()).isEqualTo(1);
    }

    @Test
    void shouldSearchEntitiesWithoutAssociationWithCriteriaDefinedInSeparateClassByUsingSubquery() {
        // given
        generateListForSearch(entityManager);

        final SubqueryConfiguration subqueryConfiguration = SubqueryConfiguration.builder()
                .rootEntity(TestEntityCollectionWithReverseAssociation.class)
                .restrictionPropertyHolder("subqueryRestrictionHolder")
                .joinBy(new SearchPropertyJoin("id", "testEntity.id")).build();

        final SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
                .subqueryConfigurationList(Collections.singletonList(subqueryConfiguration))
                .build();

        final TestEntitySearchRequest request = TestEntitySearchRequest.builder()
                .subqueryRestrictionHolder(new TestEntitySearchRequest.TestCollectionEntitySearchRequest("first0-association-1"))
                .build();

        // when
        final List<TestEntity> results = testEntitySearchRepository.findAll(request, searchConfiguration);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.size()).isEqualTo(1);
    }

    @Test
    void shouldSearchByEnumValues() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = TestEntitySearchRequest.builder()
                .testEntityEnum(TestEntityEnum.SECOND)
                .build();

        // when
        final List<TestEntity> results = testEntitySearchRepository.findAll(request, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).getTestEntityEnum()).isEqualTo(TestEntityEnum.SECOND);
    }

    @Test
    void shouldSearchByEmbeddedValues() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = TestEntitySearchRequest.builder()
                .testEntityEmbedded(new TestEntitySearchRequest.TestEntityEmbeddedSearchRequest("embedded3"))
                .build();

        // when
        final List<TestEntity> results = testEntitySearchRepository.findAll(request, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).getTestEntityEmbedded().getEmbeddedName()).isEqualTo("embedded3");
    }

    @Test
    void shouldReturnEntityListWhenNoProjectionHasBeenDefined() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = new TestEntitySearchRequest(null);

        // when
        final List<TestEntity> results = testEntitySearchRepository.findAll(request, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.get(0)).isInstanceOf(TestEntity.class);
        assertThat(results.get(0).getName()).isEqualTo("first0");
    }

    @Test
    void shouldReturnTupleListWhenUsingProjection() {
        // given
        generateListForSearch(entityManager);

        final SearchProjection<TestEntitySearchRequest> nameProjection = new SearchProjection<>("name");
        final SearchProjection<TestEntitySearchRequest> nestedNameProjection = SearchProjection.<TestEntitySearchRequest>builder().path("nestedEntity.nestedEntityName").alias("nestedName").build();

        final SearchConfiguration<TestEntity, Tuple, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, Tuple, TestEntitySearchRequest>builder()
                .resultClass(Tuple.class)
                .projectionList(Arrays.asList(nameProjection, nestedNameProjection))
                .build();

        final TestEntitySearchRequest request = new TestEntitySearchRequest(null);

        // when
        final List<Tuple> results = testEntitySearchRepository.findAll(request, searchConfiguration);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.get(0)).isInstanceOf(Tuple.class);
        assertThat(results.get(0).get("name")).isEqualTo("first0");
        assertThat(results.get(0).get("nestedName")).isEqualTo("nested0");
    }

    @Test
    void shouldReturnDtoListWhenUsingProjection() {
        // given
        generateListForSearch(entityManager);

        final SearchProjection<TestEntitySearchRequest> nameProjection = new SearchProjection<>("name");
        final SearchProjection<TestEntitySearchRequest> nestedNameProjection = SearchProjection.<TestEntitySearchRequest>builder().path("nestedEntity.nestedEntityName").alias("nestedName").build();

        final SearchConfiguration<TestEntity, TestEntityDto, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntityDto, TestEntitySearchRequest>builder()
                .resultClass(TestEntityDto.class)
                .projectionList(Arrays.asList(nameProjection, nestedNameProjection))
                .build();

        final TestEntitySearchRequest request = new TestEntitySearchRequest(null);

        // when
        final List<TestEntityDto> results = testEntitySearchRepository.findAll(request, searchConfiguration);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.get(0)).isInstanceOf(TestEntityDto.class);
    }

    @Test
    void shouldCountByRootEntityStringAttributes() {
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
                .resultClass(TestEntityDto.class)
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
    void shouldSortEntities() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = new TestEntitySearchRequest(null);

        // when
        final List<TestEntity> results = testEntitySearchRepository.findAll(request, SearchConfiguration.emptyConfiguration(), Sort.by(Sort.Order.desc("age")));

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
        final Page<TestEntity> results = testEntitySearchRepository.findAll(request, SearchConfiguration.emptyConfiguration(), PageRequest.of(0, 1));

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
        final Page<TestEntity> results = testEntitySearchRepository.findAll(request, SearchConfiguration.emptyConfiguration(), Pageable.unpaged());

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.getTotalPages()).isEqualTo(1);
        assertThat(results.getContent()).hasSize(5);
    }

    @Test
    void shouldNotFailWhenThereIsNoContent() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = new TestEntitySearchRequest("non existing name");

        // when
        final Page<TestEntity> results = testEntitySearchRepository.findAll(request, SearchConfiguration.emptyConfiguration(), PageRequest.of(0, 1));

        // then
        assertThat(results).isEmpty();
        assertThat(results.getTotalPages()).isEqualTo(0);
        assertThat(results.getContent()).hasSize(0);
    }

    @Test
    void shouldFindOneEntity() {
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
    void shouldReturnTrueWhenEntityExists() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = new TestEntitySearchRequest("first1");

        // when
        final boolean result = testEntitySearchRepository.exists(request, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldSortByJoinedEntity() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = new TestEntitySearchRequest(null);

        // when
        final List<TestEntity> results = testEntitySearchRepository.findAll(request, SearchConfiguration.emptyConfiguration(), Sort.by(Sort.Order.desc("nestedEntity.nestedEntityName")));

        // then
        assertThat(results).isNotEmpty();
    }

    @Test
    void shouldResolveRootEntityFromRootEntityResolver() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = new TestEntitySearchRequest("FIRst1");

        final SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
                .rootEntityResolver(new DefaultRootEntityResolver<>(TestEntity.class))
                .build();

        // when
        final List<TestEntity> results = testEntitySearchRepository.findAll(request, searchConfiguration);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.size()).isEqualTo(1);
    }

    @Test
    void shouldApplyJoinsToQuery() {
        // given
        generateListForSearch(entityManager);

        final TestEntitySearchRequest request = new TestEntitySearchRequest("FIRst1");

        final SearchJoin<TestEntitySearchRequest> nestedEntityJoin = SearchJoin.<TestEntitySearchRequest>builder().alias("nestedJoinAlias").path("nestedEntity").joinType(JoinType.LEFT).build();
        final SearchJoin<TestEntitySearchRequest> nonAppliedJoin = SearchJoin.<TestEntitySearchRequest>builder().alias("nestedJoinAlias").path("nonExistingPath").condition(value -> false).joinType(JoinType.LEFT).build();

        final SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
                .joinList(Arrays.asList(nestedEntityJoin, nonAppliedJoin))
                .build();

        // when
        final List<TestEntity> results = testEntitySearchRepository.findAll(request, searchConfiguration);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.size()).isEqualTo(1);
    }
}
