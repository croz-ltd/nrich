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

package net.croz.nrich.registry.data.util;

import net.croz.nrich.registry.RegistryTestConfiguration;
import net.croz.nrich.registry.data.util.stub.HibernateUtilTestEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import static net.croz.nrich.registry.data.util.testutil.HibernateUtilGeneratingUtil.createAndSaveHibernateUtilTestEntity;
import static net.croz.nrich.registry.testutil.PersistenceTestUtil.executeInTransaction;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

@SpringJUnitWebConfig(RegistryTestConfiguration.class)
class HibernateUtilTest {

    @Autowired
    private PlatformTransactionManager transactionManager;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldInitializeEntity() {
        // given
        HibernateUtilTestEntity entity = executeInTransaction(transactionManager, () -> createAndSaveHibernateUtilTestEntity(entityManager));

        // when
        HibernateUtilTestEntity loadedEntity = loadAndInitializeEntity(entity.getId());

        // and when
        Exception exception = catchException(() -> {
            loadedEntity.getParent().getId();
            loadedEntity.getChildren().get(0).getId();
        });

        // then
        assertThat(exception).isNull();
    }

    private HibernateUtilTestEntity loadAndInitializeEntity(Long id) {
        return executeInTransaction(transactionManager, () -> {
            HibernateUtilTestEntity loadedEntity = entityManager.find(HibernateUtilTestEntity.class, id);

            HibernateUtil.initialize(loadedEntity);

            return loadedEntity;
        });
    }
}
