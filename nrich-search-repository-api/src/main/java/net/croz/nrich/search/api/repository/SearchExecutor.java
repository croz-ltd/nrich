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
import java.util.Optional;

/**
 * Allows for execution of queries built from search request and configured by search configuration.
 *
 * @param <T> entity type
 */
public interface SearchExecutor<T> {

    /**
     * Returns a single entity that matches conditions applied from search request or {@link Optional#empty()} if none was found.
     *
     * @param request             search request that contains query values
     * @param searchConfiguration configuration that decides how query should be built
     * @param <R>                 type of request
     * @param <P>                 projection class
     * @return a single entity matching conditions or {@link Optional#empty()} if none was found.
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException if the query returns more than one result.
     */
    <R, P> Optional<P> findOne(R request, SearchConfiguration<T, P, R> searchConfiguration);

    /**
     * Returns all entities matching conditions applied from search request.
     *
     * @param request             search request that contains query values
     * @param searchConfiguration configuration that decides how query should be built
     * @param <R>                 type of request
     * @param <P>                 projection class
     * @return all entities matching the given conditions applied from search request
     */
    <R, P> List<P> findAll(R request, SearchConfiguration<T, P, R> searchConfiguration);

    /**
     * Returns all entities matching conditions applied from search request sorted by sort parameter.
     *
     * @param request             search request that contains query values
     * @param searchConfiguration configuration that decides how query should be built
     * @param sort                the {@link Sort} specification to sort the results by, must not be {@literal null}.
     * @param <R>                 type of request
     * @param <P>                 projection class
     * @return all entities matching the given conditions applied from search request
     */
    <R, P> List<P> findAll(R request, SearchConfiguration<T, P, R> searchConfiguration, Sort sort);

    /**
     * Returns a {@link Page} of entities matching conditions applied from search request. In case no match could be found, an empty {@link Page} is returned.
     *
     * @param request             search request that contains query values
     * @param searchConfiguration configuration that decides how query should be built
     * @param pageable            can be {@literal null}.
     * @param <R>                 type of request
     * @param <P>                 projection class
     * @return a {@link Page} of entities matching the given conditions applied from search request
     */
    <R, P> Page<P> findAll(R request, SearchConfiguration<T, P, R> searchConfiguration, Pageable pageable);

    /**
     * Returns the number of instances matching conditions applied from search request.
     *
     * @param request             search request that contains query values
     * @param searchConfiguration configuration that decides how query should be built
     * @param <R>                 type of request
     * @param <P>                 projection class
     * @return the number of instances matching conditions applied from search request.
     */
    <R, P> long count(R request, SearchConfiguration<T, P, R> searchConfiguration);

    /**
     * Whether the data store contains elements matching conditions applied from search request.
     *
     * @param request             search request that contains query values
     * @param searchConfiguration configuration that decides how query should be built
     * @param <R>                 type of request
     * @param <P>                 projection class
     * @return {@literal true} if the data store contains elements matching conditions applied from search request.
     */
    <R, P> boolean exists(R request, SearchConfiguration<T, P, R> searchConfiguration);

}
