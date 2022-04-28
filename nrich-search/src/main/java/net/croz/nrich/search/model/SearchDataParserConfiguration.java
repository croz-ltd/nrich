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

package net.croz.nrich.search.model;

import lombok.Builder;
import lombok.Getter;
import net.croz.nrich.search.api.model.SearchConfiguration;
import net.croz.nrich.search.api.model.operator.SearchOperatorOverride;
import net.croz.nrich.search.api.model.property.SearchPropertyConfiguration;
import net.croz.nrich.search.api.model.property.SearchPropertyMapping;

import java.util.List;

@Getter
@Builder
public class SearchDataParserConfiguration {

    private final boolean resolvePropertyMappingUsingPrefix;

    private final List<SearchPropertyMapping> propertyMappingList;

    private final List<SearchOperatorOverride> searchOperatorOverrideList;

    private final SearchPropertyConfiguration searchPropertyConfiguration;

    public static SearchDataParserConfiguration fromSearchConfiguration(SearchConfiguration<?, ?, ?> searchConfiguration) {
        return new SearchDataParserConfiguration(
            searchConfiguration.isResolvePropertyMappingUsingPrefix(), searchConfiguration.getPropertyMappingList(),
            searchConfiguration.getSearchOperatorOverrideList(), searchConfiguration.getSearchPropertyConfiguration()
        );
    }
}
