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

package net.croz.nrich.registry.enumadata.stub;

import java.util.List;

@SuppressWarnings("unused")
public enum RegistryTestEnumWithAdditionalData {
    FIRST_WITH_ADDITIONAL_METHODS {
        @Override
        public List<String> firstAdditionalMethod() {
            return List.of("additional method first value");
        }
    },
    SECOND_WITH_ADDITIONAL_METHODS {
        @Override
        public List<String> firstAdditionalMethod() {
            return List.of("additional method second value");
        }
    };

    public static final List<String> ADDITIONAL_METHODS_FOR_SERIALIZATION = List.of("firstAdditionalMethod", "nonExistingAdditionalMethod");

    public abstract List<String> firstAdditionalMethod();
}
