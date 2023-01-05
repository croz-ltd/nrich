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

package net.croz.nrich.webmvc.api.service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Resolves auxiliary data for exception (i.e UUID, request uri etc)
 */
public interface ExceptionAuxiliaryDataResolverService {

    /**
     * Returns map containing auxiliary data for exception and request.
     *
     * @param exception for which to resolve auxiliary data
     * @param request   current http request
     * @return map of auxiliary data
     */
    Map<String, Object> resolveRequestExceptionAuxiliaryData(Exception exception, HttpServletRequest request);

}
