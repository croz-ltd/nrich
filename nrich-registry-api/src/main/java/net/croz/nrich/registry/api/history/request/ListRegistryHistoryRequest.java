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

package net.croz.nrich.registry.api.history.request;

import net.croz.nrich.search.api.model.sort.SortProperty;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Request holding data for fetching history list of registry entities.
 *
 * @param classFullName    Class name of registry entity.
 * @param pageNumber       Page number.
 * @param pageSize         Number of entities to fetch.
 * @param registryRecordId Optional id of registry if not set history for all registry entities of specific type will be fetched.
 * @param sortPropertyList List of {@link SortProperty} instances.
 */
public record ListRegistryHistoryRequest(@NotNull String classFullName, @Min(0) @NotNull Integer pageNumber, @Max(100) @NotNull Integer pageSize, Object registryRecordId,
                                         List<SortProperty> sortPropertyList) {

}
