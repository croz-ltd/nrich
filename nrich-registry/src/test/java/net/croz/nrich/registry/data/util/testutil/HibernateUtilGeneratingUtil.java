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

package net.croz.nrich.registry.data.util.testutil;

import net.croz.nrich.registry.data.util.stub.HibernateUtilTestEntity;

import jakarta.persistence.EntityManager;
import java.util.List;

public final class HibernateUtilGeneratingUtil {

    private HibernateUtilGeneratingUtil() {
    }

    public static HibernateUtilTestEntity createAndSaveHibernateUtilTestEntity(EntityManager entityManager) {
        HibernateUtilTestEntity firstChild = createHibernateUtilTestEntityInternal(entityManager, "first", null, null);
        HibernateUtilTestEntity secondChild = createHibernateUtilTestEntityInternal(entityManager, "second", null, null);
        HibernateUtilTestEntity parent = createHibernateUtilTestEntityInternal(entityManager, "parent", null, null);

        return createHibernateUtilTestEntityInternal(entityManager, "main", parent, List.of(firstChild, secondChild));
    }

    private static HibernateUtilTestEntity createHibernateUtilTestEntityInternal(EntityManager entityManager, String name, HibernateUtilTestEntity parent, List<HibernateUtilTestEntity> children) {
        HibernateUtilTestEntity entity = new HibernateUtilTestEntity();

        entity.setName(name);
        entity.setParent(parent);
        entity.setChildren(children);

        entityManager.persist(entity);

        return entity;
    }
}
