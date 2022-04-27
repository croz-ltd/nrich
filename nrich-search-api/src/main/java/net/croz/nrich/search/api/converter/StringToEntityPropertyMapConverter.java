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

package net.croz.nrich.search.api.converter;

import net.croz.nrich.search.api.model.property.SearchPropertyConfiguration;

import javax.persistence.metamodel.ManagedType;
import java.util.List;
import java.util.Map;

/**
 * Converts string value to a map that contains property name and property value. List of properties to search is used
 * to find properties on a managed type, conversion is attempted to property type and if conversion succeeds property is added to resulting map.
 */
public interface StringToEntityPropertyMapConverter {

    /**
     * Returns a map containing property name and property value. Resolved from propertyToSearchList found on {@link ManagedType} that can be converted from passed in string.
     *
     * @param value                       value to convert
     * @param propertyToSearchList        list of properties to convert to
     * @param managedType                 entity managed type
     * @param searchPropertyConfiguration search property configuration
     * @return map with all properties for which conversion succeeded
     */
    Map<String, Object> convert(String value, List<String> propertyToSearchList, ManagedType<?> managedType, SearchPropertyConfiguration searchPropertyConfiguration);

}
