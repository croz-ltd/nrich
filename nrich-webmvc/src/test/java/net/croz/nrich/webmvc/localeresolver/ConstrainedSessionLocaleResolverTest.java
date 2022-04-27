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

package net.croz.nrich.webmvc.localeresolver;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Arrays;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class ConstrainedSessionLocaleResolverTest {

    @Test
    void shouldOnlyAllowSettingOfAllowedLocale() {
        // given
        ConstrainedSessionLocaleResolver constrainedSessionLocaleResolver = new ConstrainedSessionLocaleResolver("hr", Arrays.asList("hr", "en"));
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setSession(new MockHttpSession());

        // when
        constrainedSessionLocaleResolver.setLocale(request, new MockHttpServletResponse(), Locale.CHINA);

        // then
        assertThat(request.getSession()).isNotNull();
        assertThat(request.getSession().getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME)).isEqualTo(Locale.forLanguageTag("hr"));

        // and when
        constrainedSessionLocaleResolver.setLocale(request, new MockHttpServletResponse(), Locale.ENGLISH);

        // then
        assertThat(request.getSession().getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME)).isEqualTo(Locale.ENGLISH);
    }

    @Test
    void shouldSetDefaultLocaleWhenSettingNullLocale() {
        // given
        ConstrainedSessionLocaleResolver constrainedSessionLocaleResolver = new ConstrainedSessionLocaleResolver(null, Arrays.asList("hr", "zh_CN", "en"));
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setSession(new MockHttpSession());

        // when
        constrainedSessionLocaleResolver.setLocale(request, new MockHttpServletResponse(), null);

        // then
        assertThat(request.getSession()).isNotNull();
        assertThat(request.getSession().getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME)).isNotNull();
    }
}
