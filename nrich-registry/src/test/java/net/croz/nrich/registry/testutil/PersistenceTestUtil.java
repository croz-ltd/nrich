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

package net.croz.nrich.registry.testutil;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import java.util.function.Supplier;

public final class PersistenceTestUtil {

    private PersistenceTestUtil() {
    }

    public static void flushEntityManager(EntityManager entityManager) {
        entityManager.flush();
        entityManager.clear();
    }

    @SuppressWarnings("unchecked")
    public static <T> T executeInTransaction(PlatformTransactionManager transactionManager, Supplier<?> function) {
        return (T) new TransactionTemplate(transactionManager).execute(status -> function.get());
    }

    public static void executeInTransactionWithoutResult(PlatformTransactionManager transactionManager, Runnable function) {
        new TransactionTemplate(transactionManager).execute(status -> {
            function.run();

            return null;
        });
    }
}
