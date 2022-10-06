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

package net.croz.nrich.security.csrf.core.controller;

import net.croz.nrich.security.csrf.core.constants.CsrfConstants;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Used by client for csrf ping url
 */
@RequestMapping
@RestController
public class CsrfPingController {

    private static final String SUCCESS_KEY = "success";

    @RequestMapping("${nrich.security.csrf.endpoint-path:" + CsrfConstants.CSRF_DEFAULT_PING_URI + "}")
    public Map<String, Boolean> ping() {
        Map<String, Boolean> result = new HashMap<>();

        result.put(SUCCESS_KEY, true);

        return result;
    }
}
