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

package net.croz.nrich.registry.api.history.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Entity revision information.
 */
@RequiredArgsConstructor
@Getter
public class RevisionInfo {

    /**
     * Number of revision.
     */
    private final Long revisionNumber;

    /**
     * Revision timestamp.
     */
    private final Instant revisionTimestamp;

    /**
     * Revision type (ADD, MOD, DEL)
     */
    private final String revisionType;

    /**
     * Additional revision properties.
     */
    private final Map<String, Object> additionalRevisionPropertyMap;

}
