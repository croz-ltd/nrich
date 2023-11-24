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

package net.croz.nrich.search.api.repository;

import net.croz.nrich.search.api.model.SearchConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StringSearchExecutor<T> {

    /**
     * Returns a single entity that matches conditions applied from search term and property to search list or {@link Optional#empty()} if none was found.
     *
     * @param searchTerm           search term to search
     * @param propertyToSearchList properties to search
     * @param searchConfiguration  configuration that decides how query should be built
     * @param <P>                  projection class
     * @return a single entity matching conditions or {@link Optional#empty()} if none was found.
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException if the query returns more than one
     */
    <P> Optional<P> findOne(String searchTerm, List<String> propertyToSearchList, SearchConfiguration<T, P, Map<String, Object>> searchConfiguration);

    /**
     * Returns all entities matching conditions applied from search term and property to search list.
     *
     * @param searchTerm           search term to search
     * @param propertyToSearchList properties to search
     * @param searchConfiguration  configuration that decides how query should be built
     * @param <P>                  projection class
     * @return all entities matching the given conditions applied from search term and property to search list
     */
    <P> List<P> findAll(String searchTerm, List<String> propertyToSearchList, SearchConfiguration<T, P, Map<String, Object>> searchConfiguration);

    /**
     * Returns all entities matching conditions applied from search term and property to search list sorted by sort parameter.
     *
     * @param searchTerm           search term to search
     * @param propertyToSearchList properties to search
     * @param searchConfiguration  configuration that decides how query should be built
     * @param sort                 the {@link Sort} specification to sort the results by, must not be {@literal null}.
     * @param <P>                  projection class
     * @return all entities matching the given conditions applied from search term and property to search list
     */
    <P> List<P> findAll(String searchTerm, List<String> propertyToSearchList, SearchConfiguration<T, P, Map<String, Object>> searchConfiguration, Sort sort);

    /**
     * Returns a {@link Page} of entities matching conditions applied from search term and property to search list. In case no match could be found, an empty {@link Page} is returned.
     *
     * @param searchTerm           search term to search
     * @param propertyToSearchList properties to search
     * @param searchConfiguration  configuration that decides how query should be built
     * @param pageable             can be {@literal null}.
     * @param <P>                  projection class
     * @return a {@link Page} of entities matching the given conditions applied from search term and property to search list
     */
    <P> Page<P> findAll(String searchTerm, List<String> propertyToSearchList, SearchConfiguration<T, P, Map<String, Object>> searchConfiguration, Pageable pageable);

    /**
     * Returns the number of instances matching conditions applied from search term and property to search list.
     *
     * @param searchTerm           search term to search
     * @param propertyToSearchList properties to search
     * @param searchConfiguration  configuration that decides how query should be built
     * @param <P>                  projection class
     * @return the number of instances matching conditions applied from search term and property to search list.
     */
    <P> long count(String searchTerm, List<String> propertyToSearchList, SearchConfiguration<T, P, Map<String, Object>> searchConfiguration);

    /**
     * Whether the data store contains elements matching conditions applied from search term and property to search list.
     *
     * @param searchTerm           search term to search
     * @param propertyToSearchList properties to search
     * @param searchConfiguration  configuration that decides how query should be built
     * @param <P>                  projection class
     * @return {@literal true} if the data store contains elements matching conditions applied from search term and property to search list.
     */
    <P> boolean exists(String searchTerm, List<String> propertyToSearchList, SearchConfiguration<T, P, Map<String, Object>> searchConfiguration);

    /**
     * Returns repository domain class.
     *
     * @return repository domain class
     */
    Class<T> getDomainClass();
}
