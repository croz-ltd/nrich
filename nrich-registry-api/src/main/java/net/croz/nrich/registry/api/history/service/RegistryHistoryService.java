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

package net.croz.nrich.registry.api.history.service;

import net.croz.nrich.registry.api.history.model.EntityWithRevision;
import net.croz.nrich.registry.api.history.request.ListRegistryHistoryRequest;
import org.springframework.data.domain.Page;

/**
 * Fetches history for specific registry entity.
 */
public interface RegistryHistoryService {

    /**
     * Returns Spring's {@link Page} instance holding found {@link EntityWithRevision} instances.
     *
     * @param request {@link ListRegistryHistoryRequest} instance holding query information
     * @param <T>     type of registry entity
     * @return {@link Page} instance holding found {@link EntityWithRevision} instances
     */
    <T> Page<EntityWithRevision<T>> historyList(ListRegistryHistoryRequest request);

}
