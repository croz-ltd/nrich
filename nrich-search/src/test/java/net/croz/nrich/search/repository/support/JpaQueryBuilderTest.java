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

package net.croz.nrich.search.repository.support;

import net.croz.nrich.search.SearchTestConfiguration;
import net.croz.nrich.search.api.model.PluralAssociationRestrictionType;
import net.croz.nrich.search.api.model.SearchConfiguration;
import net.croz.nrich.search.api.model.SearchJoin;
import net.croz.nrich.search.api.model.SearchProjection;
import net.croz.nrich.search.api.model.operator.DefaultSearchOperator;
import net.croz.nrich.search.api.model.operator.SearchOperatorOverride;
import net.croz.nrich.search.api.model.property.SearchPropertyJoin;
import net.croz.nrich.search.api.model.property.SearchPropertyMapping;
import net.croz.nrich.search.api.model.subquery.SubqueryConfiguration;
import net.croz.nrich.search.repository.stub.TestEntity;
import net.croz.nrich.search.repository.stub.TestEntityAdditionalRestrictionResolver;
import net.croz.nrich.search.repository.stub.TestEntityCollectionWithReverseAssociation;
import net.croz.nrich.search.repository.stub.TestEntityDto;
import net.croz.nrich.search.repository.stub.TestEntityEnum;
import net.croz.nrich.search.repository.stub.TestEntityProjectionDto;
import net.croz.nrich.search.repository.stub.TestEntitySearchRequest;
import net.croz.nrich.search.repository.stub.TestEntityWithCustomId;
import net.croz.nrich.search.repository.stub.TestEntityWithEmbeddedId;
import net.croz.nrich.search.support.JpaQueryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaQuery;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.croz.nrich.search.repository.testutil.JpaSearchRepositoryExecutorGeneratingUtil.createTestEntitySearchRequestJoin;
import static net.croz.nrich.search.repository.testutil.JpaSearchRepositoryExecutorGeneratingUtil.generateListForSearch;
import static net.croz.nrich.search.repository.testutil.JpaSearchRepositoryExecutorGeneratingUtil.generateTestEntityWithCustomIdList;
import static net.croz.nrich.search.repository.testutil.JpaSearchRepositoryExecutorGeneratingUtil.generateTestEntityWithEmbeddedIdList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@Transactional
@SpringJUnitConfig(SearchTestConfiguration.class)
class JpaQueryBuilderTest {

    @PersistenceContext
    private EntityManager entityManager;

    private JpaQueryBuilder<TestEntity> jpaQueryBuilder;

    @BeforeEach
    void setup() {
        jpaQueryBuilder = new JpaQueryBuilder<>(entityManager, TestEntity.class);

        generateListForSearch(entityManager);
    }

    @Test
    void shouldSearchByRootEntityStringAttributes() {
        // given
        TestEntitySearchRequest request = new TestEntitySearchRequest("FIRst0");

        // when
        List<TestEntity> results = executeQuery(request, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(results).hasSize(1);
    }

    @Test
    void shouldSearchByRootEntityNumberRangeIncluding() {
        // given
        TestEntitySearchRequest request = TestEntitySearchRequest.builder().ageFromIncluding(20).ageToIncluding(25).build();

        // when
        List<TestEntity> results = executeQuery(request, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(results).hasSize(2);
    }

    @Test
    void shouldSearchByRootEntityNumberRange() {
        // given
        TestEntitySearchRequest request = TestEntitySearchRequest.builder().ageFrom(20).ageTo(25).build();

        // when
        List<TestEntity> results = executeQuery(request, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(results).hasSize(1);
    }

    @Test
    void shouldSearchBySimpleAssociationValues() {
        // given
        TestEntitySearchRequest request = TestEntitySearchRequest.builder()
            .nestedEntity(new TestEntitySearchRequest.TestNestedEntitySearchRequest("nested0"))
            .build();

        // when
        List<TestEntity> results = executeQuery(request, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(results).hasSize(1);
    }

    @Test
    void shouldSearchByCollectionAssociationValuesUsingExistsSubquery() {
        // given
        SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
            .pluralAssociationRestrictionType(PluralAssociationRestrictionType.EXISTS)
            .build();

        TestEntitySearchRequest request = TestEntitySearchRequest.builder()
            .collectionEntityList(new TestEntitySearchRequest.TestCollectionEntitySearchRequest("collection2"))
            .build();

        // when
        List<TestEntity> results = executeQuery(request, searchConfiguration);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getCollectionEntityList()).isNotEmpty();
        assertThat(results.get(0).getCollectionEntityList().get(0).getName()).isEqualTo("collection2");
    }

    @Test
    void shouldSearchByCollectionAssociationValuesUsingJoin() {
        // given
        SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
            .pluralAssociationRestrictionType(PluralAssociationRestrictionType.JOIN)
            .build();

        TestEntitySearchRequest request = TestEntitySearchRequest.builder()
            .collectionEntityList(new TestEntitySearchRequest.TestCollectionEntitySearchRequest("collection2"))
            .build();

        // when
        List<TestEntity> results = executeQuery(request, searchConfiguration);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getCollectionEntityList()).isNotEmpty();
        assertThat(results.get(0).getCollectionEntityList().get(0).getName()).isEqualTo("collection2");
    }

    @Test
    void shouldSearchByCollectionAssociationValuesByUsingFieldMapping() {
        // given
        SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
            .pluralAssociationRestrictionType(PluralAssociationRestrictionType.JOIN)
            .propertyMappingList(Collections.singletonList(new SearchPropertyMapping("collectionName", "collectionEntityList.name")))
            .build();

        TestEntitySearchRequest request = TestEntitySearchRequest.builder()
            .collectionName("collection2")
            .build();

        // when
        List<TestEntity> results = executeQuery(request, searchConfiguration);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getCollectionEntityList()).isNotEmpty();
        assertThat(results.get(0).getCollectionEntityList().get(0).getName()).isEqualTo("collection2");
    }

    @Test
    void shouldSearchCollectionAssociationPropertiesByUsingPropertyPrefix() {
        // given
        SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
            .pluralAssociationRestrictionType(PluralAssociationRestrictionType.JOIN)
            .resolvePropertyMappingUsingPrefix(true)
            .build();

        TestEntitySearchRequest request = TestEntitySearchRequest.builder()
            .collectionEntityListName("collection2")
            .build();

        // when
        List<TestEntity> results = executeQuery(request, searchConfiguration);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getCollectionEntityList()).isNotEmpty();
        assertThat(results.get(0).getCollectionEntityList().get(0).getName()).isEqualTo("collection2");
    }

    @Test
    void shouldSearchEmbeddedPropertiesByUsingPropertyPrefix() {
        // given
        SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
            .resolvePropertyMappingUsingPrefix(true)
            .build();

        TestEntitySearchRequest request = TestEntitySearchRequest.builder()
            .testEntityEmbeddedEmbeddedName("embedded1")
            .build();

        // when
        List<TestEntity> results = executeQuery(request, searchConfiguration);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTestEntityEmbedded().getEmbeddedName()).isEqualTo("embedded1");
    }

    @Test
    void shouldSearchEntitiesWithoutAssociationByUsingSubquery() {
        // given
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
        List<TestEntity> results = executeQuery(request, searchConfiguration);

        // then
        assertThat(results).hasSize(1);
    }

    @Test
    void shouldSearchEntitiesWithoutAssociationWithCriteriaDefinedInSeparateClassByUsingSubquery() {
        // given
        SubqueryConfiguration subqueryConfiguration = SubqueryConfiguration.builder()
            .rootEntity(TestEntityCollectionWithReverseAssociation.class)
            .restrictionPropertyHolder("subqueryRestrictionHolder")
            .joinBy(new SearchPropertyJoin("id", "testEntity.id")).build();

        SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
            .subqueryConfigurationList(Collections.singletonList(subqueryConfiguration))
            .build();

        TestEntitySearchRequest request = TestEntitySearchRequest.builder()
            .subqueryRestrictionHolder(new TestEntitySearchRequest.TestCollectionEntitySearchRequest("first0-association-1"))
            .build();

        // when
        List<TestEntity> results = executeQuery(request, searchConfiguration);

        // then
        assertThat(results).hasSize(1);
    }

    @Test
    void shouldSearchByEnumValues() {
        // given
        TestEntitySearchRequest request = TestEntitySearchRequest.builder()
            .testEntityEnum(TestEntityEnum.SECOND)
            .build();

        // when
        List<TestEntity> results = executeQuery(request, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTestEntityEnum()).isEqualTo(TestEntityEnum.SECOND);
    }

    @Test
    void shouldSearchByEmbeddedValues() {
        // given
        TestEntitySearchRequest request = TestEntitySearchRequest.builder()
            .testEntityEmbedded(new TestEntitySearchRequest.TestEntityEmbeddedSearchRequest("embedded3"))
            .build();

        // when
        List<TestEntity> results = executeQuery(request, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTestEntityEmbedded().getEmbeddedName()).isEqualTo("embedded3");
    }

    @Test
    void shouldReturnEntityListWhenNoProjectionHasBeenDefined() {
        // given
        TestEntitySearchRequest request = new TestEntitySearchRequest(null);

        // when
        List<TestEntity> results = executeQuery(request, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.get(0)).isInstanceOf(TestEntity.class);
        assertThat(results.get(0).getName()).isEqualTo("first0");
    }

    @Test
    void shouldReturnTupleListWhenUsingProjection() {
        // given
        SearchProjection<TestEntitySearchRequest> nameProjection = new SearchProjection<>("name");
        SearchProjection<TestEntitySearchRequest> nestedNameProjection = SearchProjection.<TestEntitySearchRequest>builder().path("nestedEntity.nestedEntityName").alias("nestedName").build();

        SearchConfiguration<TestEntity, Tuple, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, Tuple, TestEntitySearchRequest>builder()
            .resultClass(Tuple.class)
            .projectionList(Arrays.asList(nameProjection, nestedNameProjection))
            .build();

        TestEntitySearchRequest request = new TestEntitySearchRequest(null);

        // when
        List<Tuple> results = executeQuery(request, searchConfiguration);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.get(0)).isInstanceOf(Tuple.class);
        assertThat(results.get(0).get("name")).isEqualTo("first0");
        assertThat(results.get(0).get("nestedName")).isEqualTo("nested0");
    }

    @Test
    void shouldReturnDtoListWhenUsingProjection() {
        // given
        SearchProjection<TestEntitySearchRequest> nameProjection = new SearchProjection<>("name");
        SearchProjection<TestEntitySearchRequest> nestedNameProjection = SearchProjection.<TestEntitySearchRequest>builder().path("nestedEntity.nestedEntityName").alias("nestedName").build();

        SearchConfiguration<TestEntity, TestEntityDto, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntityDto, TestEntitySearchRequest>builder()
            .resultClass(TestEntityDto.class)
            .projectionList(Arrays.asList(nameProjection, nestedNameProjection))
            .build();

        TestEntitySearchRequest request = new TestEntitySearchRequest(null);

        // when
        List<TestEntityDto> results = executeQuery(request, searchConfiguration);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.get(0)).isInstanceOf(TestEntityDto.class);
    }

    @Test
    void shouldSortEntities() {
        // given
        TestEntitySearchRequest request = new TestEntitySearchRequest(null);

        // when
        List<TestEntity> results = executeQuery(request, SearchConfiguration.emptyConfiguration(), Sort.by(Sort.Order.desc("age")));

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getAge()).isEqualTo(28);
    }

    @Test
    void shouldSortByJoinedEntity() {
        // given
        TestEntitySearchRequest request = new TestEntitySearchRequest(null);

        // when
        List<TestEntity> results = executeQuery(request, SearchConfiguration.emptyConfiguration(), Sort.by(Sort.Order.desc("nestedEntity.nestedEntityName")));

        // then
        assertThat(results).isNotEmpty();
    }

    @Test
    void shouldResolveRootEntityFromRootEntityResolver() {
        // given
        TestEntitySearchRequest request = new TestEntitySearchRequest("FIRst1");

        SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
            .rootEntityResolver((requestInstance) -> TestEntity.class)
            .build();

        // when
        List<TestEntity> results = executeQuery(request, searchConfiguration);

        // then
        assertThat(results).hasSize(1);
    }

    @Test
    void shouldApplyJoinsToQuery() {
        // given
        TestEntitySearchRequest request = new TestEntitySearchRequest("FIRst1");
        SearchJoin<TestEntitySearchRequest> nestedEntityJoin = createTestEntitySearchRequestJoin("nestedJoinAlias", "nestedEntity", value -> true);
        SearchJoin<TestEntitySearchRequest> nonAppliedJoin = createTestEntitySearchRequestJoin("nestedJoinAlias", "nonExistingPath", value -> false);

        SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
            .joinList(Arrays.asList(nestedEntityJoin, nonAppliedJoin))
            .build();

        // when
        List<TestEntity> results = executeQuery(request, searchConfiguration);

        // then
        assertThat(results).hasSize(1);
    }

    @Test
    void shouldApplyJoinsFetchToQuery() {
        // given
        TestEntitySearchRequest request = new TestEntitySearchRequest("FIRst1");

        SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
            .joinList(Collections.singletonList(SearchJoin.leftJoinFetch("nestedEntity")))
            .build();

        // when
        List<TestEntity> results = executeQuery(request, searchConfiguration);

        // then
        assertThat(results).hasSize(1);
    }

    @Test
    void shouldRemoveFetchesWhenConvertingToCountQuery() {
        // given
        TestEntitySearchRequest request = new TestEntitySearchRequest("FIRst1");

        SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
            .joinList(Collections.singletonList(SearchJoin.leftJoinFetch("nestedEntity")))
            .build();

        // when
        CriteriaQuery<TestEntity> query = jpaQueryBuilder.buildQuery(request, searchConfiguration, Sort.unsorted());

        // and when
        CriteriaQuery<Long> countQuery = jpaQueryBuilder.convertToCountQuery(query);

        // then
        assertThat(entityManager.createQuery(countQuery).getSingleResult()).isEqualTo(1L);
    }

    @Test
    void shouldOverrideOperatorByTypeAndPath() {
        // given
        TestEntitySearchRequest request = new TestEntitySearchRequest("FIRst1");

        SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
            .propertyMappingList(Collections.singletonList(new SearchPropertyMapping("collectionName", "collectionEntityList.name")))
            .searchOperatorOverrideList(Arrays.asList(
                SearchOperatorOverride.forType(String.class, DefaultSearchOperator.EQ), SearchOperatorOverride.forPath("collectionEntityList.name", DefaultSearchOperator.LIKE))
            )
            .build();

        // when
        List<TestEntity> results = executeQuery(request, searchConfiguration);

        // then
        assertThat(results).isEmpty();

        // and when
        TestEntitySearchRequest requestWithSameName = TestEntitySearchRequest.builder().name("first1").collectionName("collection").build();

        List<TestEntity> resultsWithSameName = executeQuery(requestWithSameName, searchConfiguration);

        // then
        assertThat(resultsWithSameName).hasSize(1);
    }

    @Test
    void shouldNotFailWhenOverridingWithMultipleSearchParameters() {
        // given
        TestEntitySearchRequest request = new TestEntitySearchRequest("FIRst1");
        request.setAgeFrom(-1);

        SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
            .propertyMappingList(Collections.singletonList(new SearchPropertyMapping("collectionName", "collectionEntityList.name")))
            .searchOperatorOverrideList(Collections.singletonList(SearchOperatorOverride.forPath("collectionEntityList.name", DefaultSearchOperator.LIKE)))
            .build();

        // when
        List<TestEntity> results = executeQuery(request, searchConfiguration);

        // then
        assertThat(results).isNotEmpty();
    }

    @Test
    void shouldSupportContainsSearch() {
        // given
        TestEntitySearchRequest request = new TestEntitySearchRequest("Rst");

        SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
            .searchOperatorOverrideList(Collections.singletonList(SearchOperatorOverride.forType(String.class, DefaultSearchOperator.CONTAINS)))
            .build();

        // when
        List<TestEntity> results = executeQuery(request, searchConfiguration);

        // then
        assertThat(results).hasSize(5);
    }

    @Test
    void shouldApplyAdditionalRestrictionsToQuery() {
        // given
        TestEntitySearchRequest request = new TestEntitySearchRequest("FIRst1");

        SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
            .additionalRestrictionResolverList(Collections.singletonList(new TestEntityAdditionalRestrictionResolver(true)))
            .build();

        // when
        List<TestEntity> results = executeQuery(request, searchConfiguration);

        // then
        assertThat(results).isEmpty();

        // and when
        searchConfiguration.setAdditionalRestrictionResolverList(Collections.singletonList(new TestEntityAdditionalRestrictionResolver(false)));
        List<TestEntity> resultsWithoutRestriction = executeQuery(request, searchConfiguration);

        // then
        assertThat(resultsWithoutRestriction).hasSize(1);
    }

    @Test
    void shouldSupportSearchingByMap() {
        // given
        Map<String, Object> mapSearchRequest = new HashMap<>();
        mapSearchRequest.put("name", "FIRst0");

        // when
        List<TestEntity> results = executeQuery(mapSearchRequest, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(results).hasSize(1);
    }

    @Test
    void shouldSupportSearchingByMapWithRange() {
        // given
        Map<String, Object> mapSearchRequest = new HashMap<>();
        mapSearchRequest.put("ageFrom", 20);
        mapSearchRequest.put("ageTo", 25);

        // when
        List<TestEntity> results = executeQuery(mapSearchRequest, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(results).hasSize(1);
    }

    @Test
    void shouldSupportSearchingByPropertyList() {
        // given
        TestEntitySearchRequest request = new TestEntitySearchRequest(null);
        request.setNameSearchList(Arrays.asList("first1", "first2"));

        // when
        List<TestEntity> results = executeQuery(request, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(results).hasSize(2);
    }

    @Test
    void shouldIgnoreEmptyCollectionWhenSearching() {
        // given
        TestEntitySearchRequest request = new TestEntitySearchRequest(null);
        request.setNameSearchList(Collections.emptyList());

        // when
        List<TestEntity> results = executeQuery(request, SearchConfiguration.emptyConfiguration());

        // then
        assertThat(results).hasSizeGreaterThan(0);
    }

    @Test
    void shouldResolveProjectionsFromClass() {
        // given
        TestEntitySearchRequest request = new TestEntitySearchRequest(null);
        SearchConfiguration<TestEntity, TestEntityProjectionDto, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntityProjectionDto, TestEntitySearchRequest>builder()
            .resultClass(TestEntityProjectionDto.class)
            .build();

        // when
        List<TestEntityProjectionDto> results = executeQuery(request, searchConfiguration);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getName()).isEqualTo("first0");
        assertThat(results.get(0).getNestedName()).isEqualTo("nested0");
        assertThat(results.get(0).getNestedId()).isNotNull();
    }

    @Test
    void shouldSupportSearchingByEmptyMapMatchingAny() {
        // given
        Map<String, Object> mapSearchRequest = new HashMap<>();

        // when
        List<TestEntity> results = executeQuery(mapSearchRequest, SearchConfiguration.emptyConfigurationMatchingAny());

        // then
        assertThat(results).hasSize(5);
    }

    @Test
    void shouldSearchByMultipleAssociationValues() {
        // given
        TestEntitySearchRequest request = TestEntitySearchRequest.builder()
            .nestedEntityNestedEntityName("nested0")
            .nestedEntityNestedEntityAliasName("nested alias0")
            .build();

        // when
        List<TestEntity> results = executeQuery(request, SearchConfiguration.emptyConfigurationWithDefaultMappingResolve());

        // then
        assertThat(results).hasSize(1);
    }

    @Test
    void shouldThrowExceptionWhenUsingJoinFetchAndProjection() {
        // given
        SearchProjection<TestEntitySearchRequest> nameProjection = new SearchProjection<>("name");
        SearchProjection<TestEntitySearchRequest> nestedNameProjection = SearchProjection.<TestEntitySearchRequest>builder().path("nestedEntity.nestedEntityName").alias("nestedName").build();

        SearchConfiguration<TestEntity, TestEntityDto, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntityDto, TestEntitySearchRequest>builder()
            .resultClass(TestEntityDto.class)
            .projectionList(Arrays.asList(nameProjection, nestedNameProjection))
            .joinList(Collections.singletonList(SearchJoin.leftJoinFetch("nestedEntity")))
            .build();

        TestEntitySearchRequest request = new TestEntitySearchRequest(null);

        // when
        Throwable thrown = catchThrowable(() -> executeQuery(request, searchConfiguration));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldSupportNestedJoinFetchList() {
        // given
        generateTestEntityWithEmbeddedIdList(entityManager);

        JpaQueryBuilder<TestEntityWithEmbeddedId> testEntityWithEmbeddedIdJpaQueryBuilder = new JpaQueryBuilder<>(entityManager, TestEntityWithEmbeddedId.class);

        Map<String, Object> mapSearchRequest = new HashMap<>();
        mapSearchRequest.put("name", "name0");

        SearchConfiguration<TestEntityWithEmbeddedId, TestEntityWithEmbeddedId, Map<String, Object>> searchConfiguration = SearchConfiguration.<TestEntityWithEmbeddedId, TestEntityWithEmbeddedId, Map<String, Object>>builder()
            .joinList(Arrays.asList(SearchJoin.innerJoinFetch("id.firstKey"), SearchJoin.innerJoin("id.secondKey")))
            .build();

        // when
        List<TestEntityWithEmbeddedId> results = entityManager.createQuery(testEntityWithEmbeddedIdJpaQueryBuilder.buildQuery(mapSearchRequest, searchConfiguration, Sort.unsorted())).getResultList();

        // then
        assertThat(results).hasSize(1);
    }

    @Test
    void shouldSupportSearchingByAnyLevelOfNestedPath() {
        // given
        TestEntitySearchRequest request = TestEntitySearchRequest.builder().nestedEntityDoubleNestedEntityName("double nested1").build();

        // when
        List<TestEntity> results = executeQuery(request, SearchConfiguration.emptyConfigurationWithDefaultMappingResolve());

        // then
        assertThat(results).hasSize(1);
    }

    @Test
    void shouldNotFailWhenAttributeByPrefixDoesntExist() {
        // given
        TestEntitySearchRequest request = TestEntitySearchRequest.builder().nestedEntityNonExisting("non existing").build();

        // when
        Throwable thrown = catchThrowable(() -> executeQuery(request, SearchConfiguration.emptyConfigurationWithDefaultMappingResolve()));

        // then
        assertThat(thrown).isNull();
    }

    @Test
    void shouldNotFailWhenDoubleNestedAttributeByPrefixDoesntExist() {
        // given
        TestEntitySearchRequest request = TestEntitySearchRequest.builder().nestedEntityDoubleNestedEntityNonExisting("double nested1").build();

        // when
        Throwable thrown = catchThrowable(() -> executeQuery(request, SearchConfiguration.emptyConfigurationWithDefaultMappingResolve()));

        // then
        assertThat(thrown).isNull();
    }

    @Test
    void shouldFindByElementCollection() {
        // given
        TestEntitySearchRequest request = TestEntitySearchRequest.builder()
            .elementCollection("Element collection 11")
            .build();

        // when
        List<TestEntity> results = executeQuery(request, SearchConfiguration.emptyConfigurationWithDefaultMappingResolve());

        // then
        assertThat(results).hasSize(1);
    }

    @Test
    void shouldSupportJoinsByDifferentIdProperty() {
        // given
        generateTestEntityWithCustomIdList(entityManager);

        JpaQueryBuilder<TestEntityWithCustomId> testEntityWithCustomIdQueryBuilder = new JpaQueryBuilder<>(entityManager, TestEntityWithCustomId.class);

        Map<String, Object> mapSearchRequest = new HashMap<>();
        mapSearchRequest.put("enumElementCollection", TestEntityEnum.FIRST);

        SearchConfiguration<TestEntityWithCustomId, TestEntityWithCustomId, Map<String, Object>> searchConfiguration = SearchConfiguration.<TestEntityWithCustomId, TestEntityWithCustomId, Map<String, Object>>builder()
            .build();

        // when
        List<TestEntityWithCustomId> results = entityManager.createQuery(testEntityWithCustomIdQueryBuilder.buildQuery(mapSearchRequest, searchConfiguration, Sort.unsorted())).getResultList();

        // then
        assertThat(results).hasSize(2);
    }

    @Test
    void shouldUseSameJoinsForAllSubPaths() {
        // given
        TestEntitySearchRequest request = TestEntitySearchRequest.builder()
            .build();

        SearchConfiguration<TestEntity, TestEntity, TestEntitySearchRequest> searchConfiguration = SearchConfiguration.<TestEntity, TestEntity, TestEntitySearchRequest>builder()
            .joinList(Arrays.asList(SearchJoin.innerJoin("nestedEntity.doubleNestedEntity"), SearchJoin.innerJoin("nestedEntity.secondDoubleNestedEntity")))
            .resolvePropertyMappingUsingPrefix(true)
            .build();

        // when
        CriteriaQuery<TestEntity> result = jpaQueryBuilder.buildQuery(request, searchConfiguration, Sort.unsorted());

        // then
        assertThat(result.getRoots().iterator().next().getJoins()).hasSize(1);
    }

    private <P, R> List<P> executeQuery(R request, SearchConfiguration<TestEntity, P, R> searchConfiguration) {
        return executeQuery(request, searchConfiguration, Sort.unsorted());
    }

    private <P, R> List<P> executeQuery(R request, SearchConfiguration<TestEntity, P, R> searchConfiguration, Sort sort) {
        return entityManager.createQuery(jpaQueryBuilder.buildQuery(request, searchConfiguration, sort)).getResultList();
    }
}
