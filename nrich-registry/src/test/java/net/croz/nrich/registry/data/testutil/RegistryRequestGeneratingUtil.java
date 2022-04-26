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

package net.croz.nrich.registry.data.testutil;

import net.croz.nrich.registry.data.request.CreateRegistryRequest;
import net.croz.nrich.registry.data.request.UpdateRegistryRequest;

public final class RegistryRequestGeneratingUtil {

    private RegistryRequestGeneratingUtil() {
    }

    public static CreateRegistryRequest createCreateRegistryRequest(Class<?> type) {
        CreateRegistryRequest request = new CreateRegistryRequest();

        request.setClassFullName(type.getName());
        request.setJsonEntityData("{}");

        return request;
    }

    public static UpdateRegistryRequest createUpdateRegistryRequest(Class<?> type) {
        UpdateRegistryRequest request = new UpdateRegistryRequest();

        request.setClassFullName(type.getName());
        request.setJsonEntityData("{}");

        return request;
    }
}
