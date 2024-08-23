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

import java.util.Collections;
import java.util.List;

/**
 * Holder for configuration that overrides default values for each entity.
 */
@Setter
@Getter
public class RegistryOverrideConfiguration {

    /**
     * Whether this entity is read only.
     */
    private boolean readOnly = false;

    /**
     * Whether new instances of this entity can be created.
     */
    private boolean creatable = true;

    /**
     * Whether this entity can be updated.
     */
    private boolean updateable = true;

    /**
     * Whether this entity can be deleted.
     */
    private boolean deletable = true;

    /**
     * Whether history for this entity is available.
     */
    private boolean isHistoryAvailable = false;

    /**
     * List of properties that should be excluded from configuration.
     */
    private List<String> ignoredPropertyList = Collections.emptyList();

    /**
     * Order of properties.
     */
    private List<String> propertyDisplayOrderList = Collections.emptyList();

    /**
     * List of non-editable properties.
     */
    private List<String> nonEditablePropertyList = Collections.emptyList();

    /**
     * List of non sortable properties.
     */
    private List<String> nonSortablePropertyList = Collections.emptyList();

    /**
     * List of non-searchable properties.
     */
    private List<String> nonSearchablePropertyList = Collections.emptyList();

}
