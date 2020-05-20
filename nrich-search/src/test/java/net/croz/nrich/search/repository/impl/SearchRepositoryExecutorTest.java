package net.croz.nrich.search.repository.impl;

import net.croz.nrich.search.SearchConfigurationTestConfiguration;
import net.croz.nrich.search.model.DefaultRootEntityResolver;
import net.croz.nrich.search.model.PluralAssociationRestrictionType;
import net.croz.nrich.search.model.SearchConfiguration;
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
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.croz.nrich.search.repository.testutil.SearchRepositoryGeneratingUtil.generateListForSearch;
import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = SearchConfigurationTestConfiguration.class)
@Transactional
public class SearchRepositoryExecutorTest {

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

        final SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
                .rootEntityResolver(new DefaultRootEntityResolver<>(TestEntity.class))
                .build();

        final TestEntitySearchRequest request = new TestEntitySearchRequest("FIRst0");

        // when
        final List<TestEntity> results = testEntitySearchRepository.findAll(request, searchConfiguration);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.size()).isEqualTo(1);
    }

    @Test
    void shouldSearchByRootEntityNumberRangeIncluding() {
        // given
        generateListForSearch(entityManager);

        final SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
                .rootEntityResolver(new DefaultRootEntityResolver<>(TestEntity.class))
                .build();

        final TestEntitySearchRequest request = TestEntitySearchRequest.builder().ageFromIncluding(20).ageToIncluding(25).build();

        // when
        final List<TestEntity> results = testEntitySearchRepository.findAll(request, searchConfiguration);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.size()).isEqualTo(2);
    }

    @Test
    void shouldSearchByRootEntityNumberRange() {
        // given
        generateListForSearch(entityManager);

        final SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
                .rootEntityResolver(new DefaultRootEntityResolver<>(TestEntity.class))
                .build();

        final TestEntitySearchRequest request = TestEntitySearchRequest.builder().ageFrom(20).ageTo(25).build();

        // when
        final List<TestEntity> results = testEntitySearchRepository.findAll(request, searchConfiguration);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.size()).isEqualTo(1);
    }

    @Test
    void shouldSearchBySimpleAssociationValues() {
        // given
        generateListForSearch(entityManager);

        final SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
                .rootEntityResolver(new DefaultRootEntityResolver<>(TestEntity.class))
                .build();

        final TestEntitySearchRequest request = TestEntitySearchRequest.builder()
                .nestedEntity(new TestEntitySearchRequest.TestNestedEntitySearchRequest("nested0"))
                .build();

        // when
        final List<TestEntity> results = testEntitySearchRepository.findAll(request, searchConfiguration);

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
                .rootEntityResolver(new DefaultRootEntityResolver<>(TestEntity.class))
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
                .rootEntityResolver(new DefaultRootEntityResolver<>(TestEntity.class))
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
                .rootEntityResolver(new DefaultRootEntityResolver<>(TestEntity.class))
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
                .rootEntityResolver(new DefaultRootEntityResolver<>(TestEntity.class))
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
                .rootEntityResolver(new DefaultRootEntityResolver<>(TestEntity.class))
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
                .rootEntityResolver(new DefaultRootEntityResolver<>(TestEntity.class))
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

        final SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
                .rootEntityResolver(new DefaultRootEntityResolver<>(TestEntity.class))
                .build();

        final TestEntitySearchRequest request = TestEntitySearchRequest.builder()
                .testEntityEnum(TestEntityEnum.SECOND)
                .build();

        // when
        final List<TestEntity> results = testEntitySearchRepository.findAll(request, searchConfiguration);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).getTestEntityEnum()).isEqualTo(TestEntityEnum.SECOND);
    }

    @Test
    void shouldSearchByEmbeddedValues() {
        // given
        generateListForSearch(entityManager);

        final SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
                .rootEntityResolver(new DefaultRootEntityResolver<>(TestEntity.class))
                .build();

        final TestEntitySearchRequest request = TestEntitySearchRequest.builder()
                .testEntityEmbedded(new TestEntitySearchRequest.TestEntityEmbeddedSearchRequest("embedded3"))
                .build();

        // when
        final List<TestEntity> results = testEntitySearchRepository.findAll(request, searchConfiguration);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).getTestEntityEmbedded().getEmbeddedName()).isEqualTo("embedded3");
    }

    @Test
    void shouldReturnEntityListWhenNoProjectionHasBeenDefined() {
        // given
        generateListForSearch(entityManager);

        final SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
                .rootEntityResolver(new DefaultRootEntityResolver<>(TestEntity.class))
                .build();
        final TestEntitySearchRequest request = new TestEntitySearchRequest(null);

        // when
        final List<TestEntity> results = testEntitySearchRepository.findAll(request, searchConfiguration);

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
                .rootEntityResolver(new DefaultRootEntityResolver<>(TestEntity.class)).resultClass(Tuple.class)
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
                .rootEntityResolver(new DefaultRootEntityResolver<>(TestEntity.class)).resultClass(TestEntityDto.class)
                .projectionList(Arrays.asList(nameProjection, nestedNameProjection))
                .build();

        final TestEntitySearchRequest request = new TestEntitySearchRequest(null);

        // when
        final List<TestEntityDto> results = testEntitySearchRepository.findAll(request, searchConfiguration);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.get(0)).isInstanceOf(TestEntityDto.class);
    }

}
