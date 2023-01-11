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

package net.croz.nrich.registry.data.constant;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class RegistryDataConstants {

    public static final String CREATE_REQUEST_CLASS_NAME_FORMAT = "%sCreateRequest";

    public static final String UPDATE_REQUEST_CLASS_NAME_FORMAT = "%sUpdateRequest";

    public static final String REQUEST_CLASS_NAME_FORMAT = "%sRequest";

    public static final String REGISTRY_FORM_ID_FORMAT = "%s:::%s";

    public static final String REGISTRY_FORM_ID_CREATE_SUFFIX = "create";

    public static final String REGISTRY_FORM_ID_UPDATE_SUFFIX = "update";

    public static final String REQUEST_CLASS_PACKAGE_NAME = "request";

    public static final String PACKAGE_SEPARATOR = "\\.";

    public static final List<String> CLASS_NAME_SUFFIX_LIST_TO_REPLACE = Collections.unmodifiableList(Arrays.asList("model", "entity"));

    private RegistryDataConstants() {
    }
}
