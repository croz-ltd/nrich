package net.croz.nrich.search.repository.testutil;

import net.croz.nrich.search.repository.stub.TestCollectionEntity;
import net.croz.nrich.search.repository.stub.TestEntity;
import net.croz.nrich.search.repository.stub.TestEntityCollectionWithReverseAssociation;
import net.croz.nrich.search.repository.stub.TestEntityEmbedded;
import net.croz.nrich.search.repository.stub.TestEntityEnum;
import net.croz.nrich.search.repository.stub.TestNestedEntity;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class SearchRepositoryGeneratingUtil {

    private SearchRepositoryGeneratingUtil() {
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

        testEntityList.forEach(testEntity -> {

            IntStream.range(0, 2).forEach(value -> {
                final TestEntityCollectionWithReverseAssociation association = new TestEntityCollectionWithReverseAssociation(null, testEntity.getName() + "-association-" + value, testEntity);
                entityManager.persist(association);

            });

        });

        return testEntityList;
    }
}
