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

package net.croz.nrich.registry.core.constants;

public final class RegistryQueryConstants {

    public static final String PATH_SEPARATOR_REGEX = "\\.";

    public static final String ENTITY_ALIAS = "entity";

    public static final String PROPERTY_SPACE_FORMAT = " %s %s ";

    public static final String QUERY_PARAMETER_FORMAT = ENTITY_ALIAS + ".%s = :%s";

    public static final String FIND_QUERY = "select " + ENTITY_ALIAS + " from %s where %s";

    public static final String FIND_QUERY_SEPARATOR = " and ";

    private RegistryQueryConstants() {
    }
}
