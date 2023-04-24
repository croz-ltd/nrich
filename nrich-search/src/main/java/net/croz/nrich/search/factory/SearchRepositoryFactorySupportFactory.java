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

package net.croz.nrich.search.factory;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.search.api.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.api.factory.RepositoryFactorySupportFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import jakarta.persistence.EntityManager;

@RequiredArgsConstructor
public class SearchRepositoryFactorySupportFactory implements RepositoryFactorySupportFactory {

    private final StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter;

    @Override
    public RepositoryFactorySupport createRepositoryFactory(Class<?> repositoryInterface, EntityManager entityManager) {
        return new SearchRepositoryJpaRepositoryFactory(entityManager, stringToEntityPropertyMapConverter);
    }
}
