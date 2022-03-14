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

package net.croz.nrich.registry.api.core.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Configuration for a registry group. Defines group id and a list of entities that are included in the group.
 */
@Setter
@Getter
public class RegistryGroupDefinitionConfiguration {

    /**
     * Unique id of group.
     */
    private String groupId;

    /**
     * List of regex patterns that matches included registry entities.
     */
    private List<String> includeEntityPatternList;

    /**
     * List of regex patterns that matches excluded registry entities.
     */
    private List<String> excludeEntityPatternList;

}
