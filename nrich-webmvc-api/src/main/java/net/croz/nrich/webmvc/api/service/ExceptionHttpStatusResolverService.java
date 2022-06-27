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

package net.croz.nrich.webmvc.api.service;

// TODO for now returning Integer instead of Spring's HttpStatus to avoid dependency on spring-web, not sure about that decision

/**
 * Resolve http status for exception. If invalid status value is returned status 500 is used.
 */
public interface ExceptionHttpStatusResolverService {

    /**
     * Returns http status value.
     *
     * @param exception exception to resolve status for
     * @return status value
     */
    Integer resolveHttpStatusForException(Exception exception);

}
