package net.croz.nrich.search.repository.testutil;

import net.croz.nrich.search.api.model.SearchJoin;
import net.croz.nrich.search.repository.stub.TestCollectionEntity;
import net.croz.nrich.search.repository.stub.TestEntity;
import net.croz.nrich.search.repository.stub.TestEntityCollectionWithReverseAssociation;
import net.croz.nrich.search.repository.stub.TestEntityEmbedded;
import net.croz.nrich.search.repository.stub.TestEntityEnum;
import net.croz.nrich.search.repository.stub.TestEntitySearchRequest;
import net.croz.nrich.search.repository.stub.TestEntityWithEmbeddedId;
import net.croz.nrich.search.repository.stub.TestNestedEntity;
import net.croz.nrich.search.repository.stub.TestStringSearchEntity;

import javax.persistence.EntityManager;
import javax.persistence.criteria.JoinType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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

    private static TestEntity createTestEntity(Integer value, Integer numberOfCollectionEntities) {
        TestNestedEntity nestedEntity = createTestNestedEntity("nested" + value, "nested alias" + value);
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

        return entity;
    }

    private static TestNestedEntity createTestNestedEntity(String nestedEntityName, String nestedEntityAliasName) {
        TestNestedEntity entity = new TestNestedEntity();

        entity.setNestedEntityName(nestedEntityName);
        entity.setNestedEntityAliasName(nestedEntityAliasName);

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
