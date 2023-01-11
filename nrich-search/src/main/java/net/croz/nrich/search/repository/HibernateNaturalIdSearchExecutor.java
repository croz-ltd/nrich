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

package net.croz.nrich.search.repository;

import net.croz.nrich.search.api.repository.NaturalIdSearchExecutor;
import org.hibernate.NaturalIdLoadAccess;
import org.hibernate.Session;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

@Transactional(readOnly = true)
public class HibernateNaturalIdSearchExecutor<T> implements NaturalIdSearchExecutor<T> {

    private final EntityManager entityManager;

    private final Class<T> javaType;

    public HibernateNaturalIdSearchExecutor(EntityManager entityManager, Class<T> javaType) {
        this.entityManager = entityManager;
        this.javaType = javaType;
    }

    @Override
    public <I extends Serializable> Optional<T> findBySimpleNaturalId(I naturalId) {
        return resolveSession().bySimpleNaturalId(javaType).loadOptional(naturalId);
    }

    @Override
    public Optional<T> findByNaturalId(Map<String, Object> naturalId) {
        NaturalIdLoadAccess<T> naturalIdLoadAccess = resolveSession().byNaturalId(javaType);

        naturalId.forEach(naturalIdLoadAccess::using);

        return naturalIdLoadAccess.loadOptional();
    }

    private Session resolveSession() {
        return entityManager.unwrap(Session.class);
    }
}
