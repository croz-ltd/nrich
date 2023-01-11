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

package net.croz.nrich.registry.configuration.comparator;

import net.croz.nrich.registry.api.configuration.model.RegistryGroupConfiguration;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;

public class RegistryGroupConfigurationComparator extends DisplayOrderComparator implements Comparator<RegistryGroupConfiguration> {

    public RegistryGroupConfigurationComparator(List<String> propertyDisplayOrderList) {
        super(propertyDisplayOrderList);
    }

    @Override
    public int compare(RegistryGroupConfiguration firstConfiguration, RegistryGroupConfiguration secondConfiguration) {
        String firstGroupId = firstConfiguration.getGroupId();
        String secondGroupId = secondConfiguration.getGroupId();

        if (CollectionUtils.isEmpty(getPropertyDisplayOrderList())) {
            return firstGroupId.compareTo(secondGroupId);
        }

        return comparePropertiesByDisplayList(firstGroupId, secondGroupId);
    }
}
