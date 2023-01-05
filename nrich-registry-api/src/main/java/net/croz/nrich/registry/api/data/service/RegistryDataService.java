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

package net.croz.nrich.registry.api.data.service;

import net.croz.nrich.registry.api.data.request.ListBulkRegistryRequest;
import net.croz.nrich.registry.api.data.request.ListRegistryRequest;
import org.springframework.data.domain.Page;

import java.util.Map;

/**
 * Lists, creates, updates and deletes registry entities.
 */
public interface RegistryDataService {

    /**
     * Return a map holding multiple registry entities. Key is registry entity class name and value is {@link Page} of registry entities.
     *
     * @param request {@link ListBulkRegistryRequest} instance holding query information
     * @return map holding multiple registry entities
     */
    Map<String, Page<Object>> listBulk(ListBulkRegistryRequest request);

    /**
     * Returns Spring's {@link Page} instance holding found registry instances.
     *
     * @param request {@link ListRegistryRequest} instance holding query information
     * @param <P>     registry query return value (if no override is specified this is registry type, but can be a projection instance)
     * @return {@link Page} instance holding found registry instances
     */
    <P> Page<P> list(ListRegistryRequest request);

    /**
     * Returns created registry entity.
     *
     * @param classFullName Class name of registry entity
     * @param entityData    entity creation data
     * @param <T>           registry entity type
     * @return created registry instance.
     */
    <T> T create(String classFullName, Object entityData);

    /**
     * Returns update registry entity.
     *
     * @param classFullName Class name of registry entity
     * @param id            registry entity id
     * @param entityData    entity creation data
     * @param <T>           registry entity type
     * @return updated registry instance.
     */
    <T> T update(String classFullName, Object id, Object entityData);

    /**
     * Returns deleted registry entity.
     *
     * @param classFullName Class name of registry entity
     * @param id            registry entity id
     * @param <T>           registry entity type
     * @return deleted registry instance
     */
    <T> T delete(String classFullName, Object id);

}
