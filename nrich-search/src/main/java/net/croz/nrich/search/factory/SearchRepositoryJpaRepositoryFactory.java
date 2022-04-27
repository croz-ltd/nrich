/*
 *  Copyright 2020-2022 CROZ d.o.o, the original author or authors.
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

import net.croz.nrich.search.api.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.api.repository.SearchExecutor;
import net.croz.nrich.search.api.repository.StringSearchExecutor;
import net.croz.nrich.search.repository.JpaSearchExecutor;
import net.croz.nrich.search.repository.JpaStringSearchExecutor;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.core.support.RepositoryFragment;

import javax.persistence.EntityManager;
import java.io.Serializable;

public class SearchRepositoryJpaRepositoryFactory extends JpaRepositoryFactory {

    private final EntityManager entityManager;

    private final StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter;

    public SearchRepositoryJpaRepositoryFactory(EntityManager entityManager, StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter) {
        super(entityManager);
        this.entityManager = entityManager;
        this.stringToEntityPropertyMapConverter = stringToEntityPropertyMapConverter;
    }

    @Override
    protected RepositoryComposition.RepositoryFragments getRepositoryFragments(RepositoryMetadata metadata) {
        RepositoryComposition.RepositoryFragments fragments = super.getRepositoryFragments(metadata);

        if (SearchExecutor.class.isAssignableFrom(metadata.getRepositoryInterface())) {
            SearchExecutor<?> searchExecutorFragment = instantiateClass(JpaSearchExecutor.class, entityManager, getEntityInformation(metadata.getDomainType()));

            fragments = fragments.append(RepositoryFragment.implemented(SearchExecutor.class, searchExecutorFragment));
        }
        if (StringSearchExecutor.class.isAssignableFrom(metadata.getRepositoryInterface())) {
            JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainType());

            StringSearchExecutor<?> stringSearchExecutorFragment = instantiateClass(JpaStringSearchExecutor.class, stringToEntityPropertyMapConverter, entityManager, entityInformation);

            fragments = fragments.append(RepositoryFragment.implemented(StringSearchExecutor.class, stringSearchExecutorFragment));
        }

        return fragments;
    }
}
