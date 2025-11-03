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

import net.croz.nrich.search.api.converter.StringToEntityPropertyMapConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SearchRepositoryFactorySupportFactoryTest {

    @Mock
    private StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter;

    @InjectMocks
    private SearchRepositoryFactorySupportFactory searchRepositoryFactorySupportFactory;

    @Test
    void shouldCreateRepositoryFactory() {
        // given
        Class<?> type = Object.class;
        EntityManager entityManager = mock(EntityManager.class);
        EntityManagerFactory entityManagerFactory = mock(EntityManagerFactory.class);

        doReturn(entityManagerFactory).when(entityManager).getEntityManagerFactory();

        // when
        RepositoryFactorySupport result = searchRepositoryFactorySupportFactory.createRepositoryFactory(type, entityManager);

        // then
        assertThat(result).isInstanceOf(SearchRepositoryJpaRepositoryFactory.class);
        assertThat(result).extracting("stringToEntityPropertyMapConverter").isEqualTo(stringToEntityPropertyMapConverter);
    }
}
