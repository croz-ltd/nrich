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

package net.croz.nrich.registry.api.enumdata.service;

import net.croz.nrich.registry.api.enumdata.model.EnumResult;
import net.croz.nrich.registry.api.enumdata.request.ListBulkRegistryEnumRequest;
import net.croz.nrich.registry.api.enumdata.request.ListRegistryEnumRequest;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * Lists enum entities.
 */
public interface RegistryEnumService {

    /**
     * Return a map holding multiple {@link Page} instances. Key is enum class name and value is {@link Page} of {@link EnumResult} entities.
     *
     * @param request {@link ListBulkRegistryEnumRequest} instance holding query information
     * @return map holding multiple {@link EnumResult} entities
     */
    Map<String, List<EnumResult>> listBulk(ListBulkRegistryEnumRequest request);

    /**
     * Returns Spring's {@link Page} instance holding found {@link EnumResult} instances.
     *
     * @param request {@link ListRegistryEnumRequest} instance holding query information
     * @return {@link Page} instance holding found {@link EnumResult} instances
     */
    List<EnumResult> list(ListRegistryEnumRequest request);

}
