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

package net.croz.nrich.notification.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Represents validation error on a object. Returned with {@link ValidationFailureNotification} instance.
 */
@RequiredArgsConstructor
@Getter
public class ValidationError {

    public static final String CONTAINING_OBJECT_NAME = "CONTAINING_OBJECT";

    /**
     * If validation error occurred on a property then name of a property otherwise <pre>CONTAINING_OBJECT</pre>.
     */
    private final String objectName;

    /**
     * List of validation failed messages.
     */
    private final List<String> errorMessageList;

}
