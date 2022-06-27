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

package net.croz.nrich.registry.api.data.interceptor;

import net.croz.nrich.registry.api.data.request.ListRegistryRequest;

/**
 * Intercepts operations on registry entities.
 */
public interface RegistryDataInterceptor {

    /**
     * Executed before registry list operation.
     *
     * @param request {@link ListRegistryRequest} instance
     */
    void beforeRegistryList(ListRegistryRequest request);

    /**
     * Executed before registry create operation.
     *
     * @param classFullName Class name of registry entity
     * @param entityData    entity creation data
     */
    void beforeRegistryCreate(String classFullName, Object entityData);

    /**
     * Executed before registry update operation.
     *
     * @param classFullName Class name of registry entity
     * @param id            registry entity id
     * @param entityData    entity creation data
     */
    void beforeRegistryUpdate(String classFullName, Object id, Object entityData);

    /**
     * Executed before registry delete operation.
     *
     * @param classFullName Class name of registry entity
     * @param id            registry entity id
     */
    void beforeRegistryDelete(String classFullName, Object id);

}
