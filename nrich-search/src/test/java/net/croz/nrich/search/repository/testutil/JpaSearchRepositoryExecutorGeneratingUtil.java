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

package net.croz.nrich.search.repository.testutil;

import net.croz.nrich.search.api.model.SearchJoin;
import net.croz.nrich.search.repository.stub.TestCollectionEntity;
import net.croz.nrich.search.repository.stub.TestDoubleNestedEntity;
import net.croz.nrich.search.repository.stub.TestEntity;
import net.croz.nrich.search.repository.stub.TestEntityCollectionWithReverseAssociation;
import net.croz.nrich.search.repository.stub.TestEntityEmbedded;
import net.croz.nrich.search.repository.stub.TestEntityEnum;
import net.croz.nrich.search.repository.stub.TestEntitySearchRequest;
import net.croz.nrich.search.repository.stub.TestEntityWithCustomId;
import net.croz.nrich.search.repository.stub.TestEntityWithEmbeddedId;
import net.croz.nrich.search.repository.stub.TestNestedEntity;
import net.croz.nrich.search.repository.stub.TestStringSearchEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.JoinType;

import net.croz.nrich.search.repository.stub.TestSubEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class JpaSearchRepositoryExecutorGeneratingUtil {

    private JpaSearchRepositoryExecutorGeneratingUtil() {
    }

    public static List<TestEntity> generateListForSearch(EntityManager entityManager) {
        return generateListForSearch(entityManager, 1);
    }

    public static List<TestEntity> generateListForSearch(EntityManager entityManager, int numberOfCollectionEntities) {
        List<TestEntity> testEntityList = IntStream.range(0, 5)
            .mapToObj(value -> createTestEntity(value, numberOfCollectionEntities))
            .collect(Collectors.toList());

        testEntityList.get(1).setTestEntityEnum(TestEntityEnum.SECOND);

        testEntityList.forEach(entityManager::persist);

        testEntityList.forEach(testEntity -> IntStream.range(0, 2).forEach(value -> {
            TestEntityCollectionWithReverseAssociation association = createTestEntityCollectionWithReverseAssociation(testEntity.getName() + "-association-" + value, testEntity);

            entityManager.persist(association);
        }));

        return testEntityList;
    }

    public static List<TestStringSearchEntity> generateListForStringSearch(EntityManager entityManager) {
        LocalDate date = LocalDate.parse("01.01.1970", DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        List<TestStringSearchEntity> testEntityList = IntStream.range(0, 5)
            .mapToObj(value -> createTestStringSearchEntity("name " + value, 50 + value, date.plus(value, ChronoUnit.DAYS)))
            .collect(Collectors.toList());

        testEntityList.forEach(entityManager::persist);

        return testEntityList;
    }

    public static List<TestEntityWithEmbeddedId> generateTestEntityWithEmbeddedIdList(EntityManager entityManager) {
        return IntStream.range(0, 5)
            .mapToObj(value -> generateTestEntityWithEmbeddedId(entityManager, "name" + value))
            .collect(Collectors.toList());
    }

    public static TestEntityWithEmbeddedId generateTestEntityWithEmbeddedId(EntityManager entityManager, String name) {
        TestEntityWithEmbeddedId.RegistryHistoryTestEntityWithEmbeddedObjectFirstKey firstKey = new TestEntityWithEmbeddedId.RegistryHistoryTestEntityWithEmbeddedObjectFirstKey();
        TestEntityWithEmbeddedId.RegistryHistoryTestEntityWithEmbeddedObjectSecondKey secondKey = new TestEntityWithEmbeddedId.RegistryHistoryTestEntityWithEmbeddedObjectSecondKey();

        TestEntityWithEmbeddedId.TestEntityWithEmbeddedIdObjectId primaryKey = new TestEntityWithEmbeddedId.TestEntityWithEmbeddedIdObjectId();

        primaryKey.setFirstKey(firstKey);
        primaryKey.setSecondKey(secondKey);

        TestEntityWithEmbeddedId entity = new TestEntityWithEmbeddedId();

        entity.setName(name);
        entity.setId(primaryKey);

        entityManager.persist(firstKey);
        entityManager.persist(secondKey);
        entityManager.persist(entity);

        return entity;
    }

    public static SearchJoin<TestEntitySearchRequest> createTestEntitySearchRequestJoin(String alias, String path, Predicate<TestEntitySearchRequest> condition) {
        return SearchJoin.<TestEntitySearchRequest>builder()
            .alias(alias)
            .path(path)
            .condition(condition)
            .joinType(JoinType.LEFT)
            .build();
    }

    public static void generateTestEntityWithCustomIdList(EntityManager entityManager) {
        IntStream.range(0, 3).forEach(value -> {
            TestEntityWithCustomId entity = new TestEntityWithCustomId();

            entity.setEnumElementCollection(Collections.singletonList(value % 2 == 0 ? TestEntityEnum.FIRST : TestEntityEnum.SECOND));

            entityManager.persist(entity);
        });
    }

    public static void generateTestSubEntityList(EntityManager entityManager) {
        IntStream.range(0, 3).forEach(value -> {
            TestSubEntity entity = new TestSubEntity();

            entity.setSubName("subName" + value);

            entityManager.persist(entity);
        });
    }

    private static TestEntity createTestEntity(Integer value, Integer numberOfCollectionEntities) {
        TestNestedEntity nestedEntity = createTestNestedEntity(value);
        List<TestCollectionEntity> collectionEntityList = IntStream.range(0, numberOfCollectionEntities)
            .mapToObj(counter -> createTestCollectionEntity("collection" + (value + counter)))
            .collect(Collectors.toList());
        TestEntityEmbedded testEntityEmbedded = createTestEntityEmbedded("embedded" + value);

        TestEntity entity = new TestEntity();

        entity.setName("first" + value);
        entity.setAge(24 + value);
        entity.setNestedEntity(nestedEntity);
        entity.setCollectionEntityList(collectionEntityList);
        entity.setTestEntityEnum(TestEntityEnum.FIRST);
        entity.setTestEntityEmbedded(testEntityEmbedded);
        entity.setElementCollection(Arrays.asList("Element collection 1" + value, "Element collection 2" + value));

        return entity;
    }

    private static TestNestedEntity createTestNestedEntity(Integer value) {
        TestNestedEntity entity = new TestNestedEntity();

        entity.setNestedEntityName("nested" + value);
        entity.setNestedEntityAliasName("nested alias" + value);
        entity.setDoubleNestedEntity(createTestDoubleNestedEntity(value));
        entity.setRelated(Collections.singletonList(createTestDoubleNestedEntity(value)));

        return entity;
    }

    private static TestDoubleNestedEntity createTestDoubleNestedEntity(Integer value) {
        TestDoubleNestedEntity entity = new TestDoubleNestedEntity();

        entity.setName("double nested" + value);

        return entity;
    }

    private static TestCollectionEntity createTestCollectionEntity(String name) {
        TestCollectionEntity entity = new TestCollectionEntity();

        entity.setName(name);

        return entity;
    }

    private static TestEntityEmbedded createTestEntityEmbedded(String name) {
        TestEntityEmbedded entity = new TestEntityEmbedded();

        entity.setEmbeddedName(name);

        return entity;
    }

    private static TestEntityCollectionWithReverseAssociation createTestEntityCollectionWithReverseAssociation(String name, TestEntity testEntity) {
        TestEntityCollectionWithReverseAssociation entity = new TestEntityCollectionWithReverseAssociation();

        entity.setName(name);
        entity.setTestEntity(testEntity);

        return entity;
    }

    private static TestStringSearchEntity createTestStringSearchEntity(String name, Integer age, LocalDate localDate) {
        TestStringSearchEntity entity = new TestStringSearchEntity();

        entity.setName(name);
        entity.setAge(age);
        entity.setTestEntityEnum(TestEntityEnum.SECOND);
        entity.setLocalDate(localDate);

        return entity;
    }
}
