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

package net.croz.nrich.search.util;

import net.croz.nrich.search.api.model.property.SearchPropertyConfiguration;

public final class PropertyNameUtil {

    private PropertyNameUtil() {
    }

    public static String propertyNameWithoutSuffix(String originalPropertyName, SearchPropertyConfiguration searchPropertyConfiguration) {
        String[] suffixListToRemove = new String[] {
            searchPropertyConfiguration.getRangeQueryFromIncludingSuffix(), searchPropertyConfiguration.getRangeQueryFromSuffix(), searchPropertyConfiguration.getRangeQueryToIncludingSuffix(),
            searchPropertyConfiguration.getRangeQueryToSuffix(), searchPropertyConfiguration.getCollectionQuerySuffix()
        };

        String propertyName = originalPropertyName;
        for (String suffix : suffixListToRemove) {
            if (originalPropertyName.endsWith(suffix)) {
                propertyName = originalPropertyName.substring(0, originalPropertyName.lastIndexOf(suffix));
                break;
            }
        }

        return propertyName;
    }
}
