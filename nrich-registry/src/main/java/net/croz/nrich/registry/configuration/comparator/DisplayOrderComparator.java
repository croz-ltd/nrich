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

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class DisplayOrderComparator implements Serializable {

    private static final long serialVersionUID = 4980226671627040327L;

    private final List<String> propertyDisplayOrderList;

    public int comparePropertiesByDisplayList(String firstPropertyName, String secondPropertyName) {
        if (!propertyDisplayOrderList.contains(firstPropertyName)) {
            return 1;
        }

        if (!propertyDisplayOrderList.contains(secondPropertyName)) {
            return -1;
        }

        Integer firstPropertyIndex = propertyDisplayOrderList.indexOf(firstPropertyName);
        Integer secondPropertyIndex = propertyDisplayOrderList.indexOf(secondPropertyName);

        return firstPropertyIndex.compareTo(secondPropertyIndex);
    }
}
