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

package net.croz.nrich.registry.api.configuration.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Configuration for registry group (a group of registry entities).
 */
@RequiredArgsConstructor
@Getter
public class RegistryGroupConfiguration {

    /**
     * Unique id of group.
     */
    private final String groupId;

    /**
     * Display label for group.
     */
    private final String groupIdDisplayName;

    /**
     * List of entity configurations belonging to this group.
     */
    private final List<RegistryEntityConfiguration> entityConfigurationList;

}
