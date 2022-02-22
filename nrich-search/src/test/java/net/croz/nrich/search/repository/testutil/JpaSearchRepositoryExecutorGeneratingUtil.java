package net.croz.nrich.search.repository.testutil;

import net.croz.nrich.search.repository.stub.TestCollectionEntity;
import net.croz.nrich.search.repository.stub.TestEntity;
import net.croz.nrich.search.repository.stub.TestEntityCollectionWithReverseAssociation;
import net.croz.nrich.search.repository.stub.TestEntityEmbedded;
import net.croz.nrich.search.repository.stub.TestEntityEnum;
import net.croz.nrich.search.repository.stub.TestEntityWithEmbeddedId;
import net.croz.nrich.search.repository.stub.TestNestedEntity;
import net.croz.nrich.search.repository.stub.TestStringSearchEntity;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class JpaSearchRepositoryExecutorGeneratingUtil {

    private JpaSearchRepositoryExecutorGeneratingUtil() {
    }

    public static List<TestEntity> generateListForSearch(EntityManager entityManager) {
        return generateListForSearch(entityManager, 1);
    }

    public static List<TestEntity> generateListForSearch(EntityManager entityManager, int numberOfCollectionEntities) {
        List<TestEntity> testEntityList = IntStream.range(0, 5).mapToObj(value -> {
            TestNestedEntity nestedEntity = new TestNestedEntity(null, "nested" + value, "nested alias" + value);
            List<TestCollectionEntity> collectionEntityList = IntStream.range(0, numberOfCollectionEntities).mapToObj(counter -> new TestCollectionEntity(null, "collection" + (value + counter))).collect(Collectors.toList());
            TestEntityEmbedded testEntityEmbedded = new TestEntityEmbedded("embedded" + value);

            return new TestEntity(null, "first" + value, 24 + value, nestedEntity, collectionEntityList, TestEntityEnum.FIRST, testEntityEmbedded);

        }).collect(Collectors.toList());

        testEntityList.get(1).setTestEntityEnum(TestEntityEnum.SECOND);

        testEntityList.forEach(entityManager::persist);

        testEntityList.forEach(testEntity -> IntStream.range(0, 2).forEach(value -> {
            TestEntityCollectionWithReverseAssociation association = new TestEntityCollectionWithReverseAssociation(null, testEntity.getName() + "-association-" + value, testEntity);

            entityManager.persist(association);
        }));

        return testEntityList;
    }

    public static List<TestStringSearchEntity> generateListForStringSearch(EntityManager entityManager) {
        LocalDate date = LocalDate.parse("01.01.1970", DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        List<TestStringSearchEntity> testEntityList = IntStream.range(0, 5).mapToObj(value -> new TestStringSearchEntity(null, "name " + value, 50 + value, TestEntityEnum.SECOND, date.plus(value, ChronoUnit.DAYS))).collect(Collectors.toList());

        testEntityList.forEach(entityManager::persist);

        return testEntityList;
    }

    public static List<TestEntityWithEmbeddedId> generateTestEntityWithEmbeddedIdList(EntityManager entityManager) {
        return IntStream.range(0, 5).mapToObj(value -> generateTestEntityWithEmbeddedId(entityManager, "name" + value)).collect(Collectors.toList());
    }

    public static TestEntityWithEmbeddedId generateTestEntityWithEmbeddedId(EntityManager entityManager, String name) {
        TestEntityWithEmbeddedId.RegistryHistoryTestEntityWithEmbeddedObjectFirstKey firstKey = new TestEntityWithEmbeddedId.RegistryHistoryTestEntityWithEmbeddedObjectFirstKey();
        TestEntityWithEmbeddedId.RegistryHistoryTestEntityWithEmbeddedObjectSecondKey secondKey = new TestEntityWithEmbeddedId.RegistryHistoryTestEntityWithEmbeddedObjectSecondKey();

        TestEntityWithEmbeddedId.TestEntityWithEmbeddedIdObjectId primaryKey = new TestEntityWithEmbeddedId.TestEntityWithEmbeddedIdObjectId(firstKey, secondKey);
        TestEntityWithEmbeddedId entity = new TestEntityWithEmbeddedId(primaryKey, name);

        entityManager.persist(firstKey);
        entityManager.persist(secondKey);
        entityManager.persist(entity);

        return entity;
    }

}
