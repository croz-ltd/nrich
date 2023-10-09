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

package net.croz.nrich.registry.enumadata.testutil;

import net.croz.nrich.registry.api.enumdata.request.ListBulkRegistryEnumRequest;
import net.croz.nrich.registry.api.enumdata.request.ListRegistryEnumRequest;

import java.util.Collections;

public final class RegistryEnumGeneratingUtil {

    private RegistryEnumGeneratingUtil() {
    }

    public static ListBulkRegistryEnumRequest createBulkListEnumRequest(String classFullName, String query) {
        ListBulkRegistryEnumRequest request = new ListBulkRegistryEnumRequest();

        request.setRegistryRequestList(Collections.singletonList(createListEnumRequest(classFullName, query)));

        return request;
    }

    public static ListRegistryEnumRequest createListEnumRequest(String classFullName, String query) {
        ListRegistryEnumRequest request = new ListRegistryEnumRequest();

        request.setClassFullName(classFullName);
        request.setQuery(query);

        return request;
    }
}
