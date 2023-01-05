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

package net.croz.nrich.webmvc.advice.testutil;

import java.util.HashMap;
import java.util.Map;

public final class MapGeneratingUtil {

    private MapGeneratingUtil() {
    }

    public static Map<String, String> createMap(String firstKey, String firstValue, String secondKey, String secondValue) {
        Map<String, String> map = new HashMap<>();

        map.put(firstKey, firstValue);
        map.put(secondKey, secondValue);

        return map;
    }
}
