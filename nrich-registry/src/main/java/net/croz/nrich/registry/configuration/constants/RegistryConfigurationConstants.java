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

package net.croz.nrich.registry.configuration.constants;

public final class RegistryConfigurationConstants {

    public static final String REGISTRY_PROPERTY_PREFIX_FORMAT = "%s.%s";

    public static final String REGISTRY_GROUP_DISPLAY_LABEL_FORMAT = "%s.registryGroupIdDisplayName";

    public static final String REGISTRY_NAME_DISPLAY_LABEL_FORMAT = "%s.registryEntityDisplayName";

    public static final String REGISTRY_REVISION_ENTITY_PREFIX = "revisionEntity";

    public static final String REGISTRY_FIELD_DISPLAY_LABEL_FORMAT = "%s.%s.label";

    public static final String REGISTRY_FIELD_DISPLAY_LABEL_SHORT_FORMAT = "default.%s.label";

    public static final String REGISTRY_COLUMN_HEADER_FORMAT = "%s.%s.header";

    private RegistryConfigurationConstants() {
    }
}
