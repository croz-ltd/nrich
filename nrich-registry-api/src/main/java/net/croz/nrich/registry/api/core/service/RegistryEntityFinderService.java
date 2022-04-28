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

package net.croz.nrich.registry.api.core.service;

import java.util.Map;

/**
 * Entity finder service. Used by {@link net.croz.nrich.registry.api.data.service.RegistryDataService} and {@link net.croz.nrich.registry.api.history.service.RegistryHistoryService}
 * for finding entity instances and creating id conditions.
 */
public interface RegistryEntityFinderService {

    /**
     * Find entity instance of specified by id.
     *
     * @param type entity type
     * @param id   id of entity, can be a map, embedded id or simple object (Long, String)
     * @param <T>  entity type
     * @return entity instance
     * @throws javax.persistence.NoResultException        when no result has been found
     * @throws javax.persistence.NonUniqueResultException when there is more than one result
     */
    <T> T findEntityInstance(Class<T> type, Object id);

    /**
     * Converts id to parameter map where keys are id names and values are id values (i.e. for single id of type long it will be {@literal id -> 1L})
     *
     * @param type entity type
     * @param id   id of entity, can be a map, embedded id or simple object (Long, String)
     * @param <T>  entity type
     * @return id map
     */
    <T> Map<String, Object> resolveIdParameterMap(Class<T> type, Object id);

}
