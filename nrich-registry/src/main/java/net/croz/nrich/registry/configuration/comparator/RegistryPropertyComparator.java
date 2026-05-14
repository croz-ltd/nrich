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

import net.croz.nrich.registry.api.configuration.model.property.RegistryPropertyConfiguration;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class RegistryPropertyComparator extends DisplayOrderComparator implements Comparator<RegistryPropertyConfiguration> {

    public RegistryPropertyComparator(List<String> propertyDisplayOrderList) {
        super(propertyDisplayOrderList);
    }

    @Override
    public int compare(RegistryPropertyConfiguration firstProperty, RegistryPropertyConfiguration secondProperty) {
        String firstPropertyName = firstProperty.getName();
        String secondPropertyName = secondProperty.getName();
        List<String> displayOrderList = getPropertyDisplayOrderList();
        boolean eitherPropertyInDisplayOrder = !CollectionUtils.isEmpty(displayOrderList) && Stream.of(firstPropertyName, secondPropertyName).anyMatch(displayOrderList::contains);

        if (eitherPropertyInDisplayOrder) {
            return comparePropertiesByDisplayList(firstPropertyName, secondPropertyName);
        }
        if (firstProperty.isId() && !secondProperty.isId()) {
            return -1;
        }
        if (!firstProperty.isId() && secondProperty.isId()) {
            return 1;
        }

        return firstPropertyName.compareTo(secondPropertyName);
    }
}
