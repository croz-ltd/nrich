package net.croz.nrich.search.repository.testutil;

import net.croz.nrich.search.repository.stub.TestCollectionEntity;
import net.croz.nrich.search.repository.stub.TestEntity;
import net.croz.nrich.search.repository.stub.TestEntityCollectionWithReverseAssociation;
import net.croz.nrich.search.repository.stub.TestEntityEmbedded;
import net.croz.nrich.search.repository.stub.TestEntityEnum;
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

    public static List<TestEntity> generateListForSearch(final EntityManager entityManager) {
        return generateListForSearch(entityManager, 1);
    }

    public static List<TestEntity> generateListForSearch(final EntityManager entityManager, final int numberOfCollectionEntities) {
        final List<TestEntity> testEntityList = IntStream.range(0, 5).mapToObj(value -> {
            final TestNestedEntity nestedEntity = new TestNestedEntity(null, "nested" + value);
            final List<TestCollectionEntity> collectionEntityList = IntStream.range(0, numberOfCollectionEntities).mapToObj(counter -> new TestCollectionEntity(null, "collection" + (value + counter))).collect(Collectors.toList());
            final TestEntityEmbedded testEntityEmbedded = new TestEntityEmbedded("embedded" + value);

            return new TestEntity(null, "first" + value, 24 + value, nestedEntity, collectionEntityList, TestEntityEnum.FIRST, testEntityEmbedded);

        }).collect(Collectors.toList());

        testEntityList.get(1).setTestEntityEnum(TestEntityEnum.SECOND);

        testEntityList.forEach(entityManager::persist);

        testEntityList.forEach(testEntity -> IntStream.range(0, 2).forEach(value -> {
            final TestEntityCollectionWithReverseAssociation association = new TestEntityCollectionWithReverseAssociation(null, testEntity.getName() + "-association-" + value, testEntity);

            entityManager.persist(association);
        }));

        return testEntityList;
    }

    public static List<TestStringSearchEntity> generateListForStringSearch(final EntityManager entityManager) {
        final LocalDate date = LocalDate.parse("01.01.1970", DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        final List<TestStringSearchEntity> testEntityList = IntStream.range(0, 5).mapToObj(value -> new TestStringSearchEntity(null, "name " + value, 50 + value, TestEntityEnum.SECOND, date.plus(value, ChronoUnit.DAYS))).collect(Collectors.toList());

        testEntityList.forEach(entityManager::persist);

        return testEntityList;
    }
}
