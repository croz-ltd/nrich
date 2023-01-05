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

import net.croz.nrich.search.repository.stub.TestEntityWithNaturalId;
import net.croz.nrich.search.repository.stub.TestEntityWithSimpleNaturalId;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public final class HibernateNaturalIdSearchExecutorGeneratingUtil {

    private static final int NUMBER_OF_ENTITIES = 5;

    private HibernateNaturalIdSearchExecutorGeneratingUtil() {
    }

    public static void generateListForSimpleNaturalIdSearch(EntityManager entityManager) {
        IntStream.range(0, NUMBER_OF_ENTITIES)
            .mapToObj(HibernateNaturalIdSearchExecutorGeneratingUtil::createTestEntityWithSimpleNaturalId)
            .forEach(entityManager::persist);
    }

    public static void generateListForNaturalIdSearch(EntityManager entityManager) {
        IntStream.range(0, NUMBER_OF_ENTITIES)
            .mapToObj(HibernateNaturalIdSearchExecutorGeneratingUtil::createTestEntityWithNaturalId)
            .forEach(entityManager::persist);
    }

    public static Map<String, Object> createNaturalId(Object firstValue, Object secondValue) {
        Map<String, Object> naturalId = new HashMap<>();

        naturalId.put("firstNaturalId", firstValue);
        naturalId.put("secondNaturalId", secondValue);

        return naturalId;
    }

    private static TestEntityWithSimpleNaturalId createTestEntityWithSimpleNaturalId(int value) {
        TestEntityWithSimpleNaturalId entity = new TestEntityWithSimpleNaturalId();

        entity.setNaturalId("simple " + value);

        return entity;
    }

    private static TestEntityWithNaturalId createTestEntityWithNaturalId(int value) {
        TestEntityWithNaturalId entity = new TestEntityWithNaturalId();

        entity.setFirstNaturalId("first " + value);
        entity.setSecondNaturalId("second " + value);

        return entity;
    }
}
