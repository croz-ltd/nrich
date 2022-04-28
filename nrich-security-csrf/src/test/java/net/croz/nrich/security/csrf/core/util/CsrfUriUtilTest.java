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
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static net.croz.nrich.security.csrf.core.testutil.CsrfCoreGeneratingUtil.csrfExcludeConfig;
import static org.assertj.core.api.Assertions.assertThat;

class CsrfUriUtilTest {

    @Test
    void shouldReturnFalseWhenExcludedUriConfigurationIsEmpty() {
        // when
        boolean result = CsrfUriUtil.excludeUri(null, "url");

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenExcludedUriIsNotMatched() {
        // given
        CsrfExcludeConfig csrfExcludeConfig = csrfExcludeConfig("not-matched", null);

        // when
        boolean result = CsrfUriUtil.excludeUri(Collections.singletonList(csrfExcludeConfig), "url");

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenExcludedUriIsNotMatchedByRegex() {
        // given
        CsrfExcludeConfig csrfExcludeConfig = csrfExcludeConfig(null, "/*");

        // when
        boolean result = CsrfUriUtil.excludeUri(Collections.singletonList(csrfExcludeConfig), "url");

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueWhenExcludedUriIsMatched() {
        // given
        CsrfExcludeConfig csrfExcludeConfig = csrfExcludeConfig("url", null);

        // when
        boolean result = CsrfUriUtil.excludeUri(Collections.singletonList(csrfExcludeConfig), "url");

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnTrueWhenExcludedUriIsMatchedByRegex() {
        // given
        CsrfExcludeConfig csrfExcludeConfig = csrfExcludeConfig(null, ".*");

        // when
        boolean result = CsrfUriUtil.excludeUri(Collections.singletonList(csrfExcludeConfig), "url");

        // then
        assertThat(result).isTrue();
    }
}
