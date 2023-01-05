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

package net.croz.nrich.registry.api.core.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * User defined configuration for registry entities.
 */
@Setter
@Getter
public class RegistryConfiguration {

    /**
     * Order in which groups should be sorted.
     */
    private List<String> groupDisplayOrderList;

    /**
     * List of {@link RegistryGroupDefinitionConfiguration} instances that hold registry configuration.
     */
    private List<RegistryGroupDefinitionConfiguration> groupDefinitionConfigurationList;

    /**
     * List of {@link RegistryOverrideConfigurationHolder} instances that hold override configuration for registry groups.
     */
    private List<RegistryOverrideConfigurationHolder> overrideConfigurationHolderList;

    /**
     * Order of history properties.
     */
    private List<String> historyDisplayOrderList;

}
