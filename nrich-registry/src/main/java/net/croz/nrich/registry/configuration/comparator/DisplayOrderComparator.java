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

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class DisplayOrderComparator implements Serializable {

    @Serial
    private static final long serialVersionUID = 4980226671627040327L;

    private final List<String> propertyDisplayOrderList;

    public int comparePropertiesByDisplayList(String firstPropertyName, String secondPropertyName) {
        boolean firstInList = propertyDisplayOrderList.contains(firstPropertyName);
        boolean secondInList = propertyDisplayOrderList.contains(secondPropertyName);

        if (!firstInList && !secondInList) {
            return firstPropertyName.compareTo(secondPropertyName);
        }
        if (!firstInList) {
            return 1;
        }
        if (!secondInList) {
            return -1;
        }

        return Integer.compare(propertyDisplayOrderList.indexOf(firstPropertyName), propertyDisplayOrderList.indexOf(secondPropertyName));
    }
}
