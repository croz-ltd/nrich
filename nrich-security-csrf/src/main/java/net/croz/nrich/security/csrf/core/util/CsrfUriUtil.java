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

package net.croz.nrich.security.csrf.core.util;

import net.croz.nrich.security.csrf.core.model.CsrfExcludeConfig;
import org.springframework.util.CollectionUtils;

import java.util.List;

public final class CsrfUriUtil {

    private CsrfUriUtil() {
    }

    public static boolean excludeUri(List<CsrfExcludeConfig> csrfExcludeConfigList, String uri) {
        if (CollectionUtils.isEmpty(csrfExcludeConfigList)) {
            return false;
        }

        return csrfExcludeConfigList.stream()
            .anyMatch(csrfExcludeConfig ->
                csrfExcludeConfig.getUri() != null && csrfExcludeConfig.getUri().endsWith(uri) || csrfExcludeConfig.getRegex() != null && uri.matches(csrfExcludeConfig.getRegex())
            );
    }
}
