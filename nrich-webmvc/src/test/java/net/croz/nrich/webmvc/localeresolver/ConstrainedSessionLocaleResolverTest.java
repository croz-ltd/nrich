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

package net.croz.nrich.webmvc.localeresolver;

import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.context.i18n.SimpleTimeZoneAwareLocaleContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static net.croz.nrich.webmvc.localeresolver.testutil.LocaleResolverRequestGeneratingUtil.createRequest;
import static net.croz.nrich.webmvc.localeresolver.testutil.LocaleResolverRequestGeneratingUtil.createRequestWithPreferredLocale;
import static net.croz.nrich.webmvc.localeresolver.testutil.LocaleResolverRequestGeneratingUtil.createRequestWithTimeZone;
import static org.assertj.core.api.Assertions.assertThat;

class ConstrainedSessionLocaleResolverTest {

    @Test
    void shouldOnlyAllowSettingOfAllowedLocale() {
        // given
        ConstrainedSessionLocaleResolver constrainedSessionLocaleResolver = new ConstrainedSessionLocaleResolver("hr", List.of("hr", "en"));
        MockHttpServletRequest request = createRequest();

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
        ConstrainedSessionLocaleResolver constrainedSessionLocaleResolver = new ConstrainedSessionLocaleResolver(null, List.of("hr", "zh_CN", "en"));
        MockHttpServletRequest request = createRequest();

        // when
        constrainedSessionLocaleResolver.setLocale(request, new MockHttpServletResponse(), null);

        // then
        assertThat(request.getSession()).isNotNull();
        assertThat(request.getSession().getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME)).isNotNull();
    }

    @Test
    void shouldSetCountrySpecificDefaultLocaleWhenSettingUnsupportedLocale() {
        // given
        ConstrainedSessionLocaleResolver constrainedSessionLocaleResolver = new ConstrainedSessionLocaleResolver("en_US", List.of("en_US"));
        MockHttpServletRequest request = createRequest();

        // when
        constrainedSessionLocaleResolver.setLocale(request, new MockHttpServletResponse(), Locale.GERMANY);

        // then
        assertThat(request.getSession().getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME)).isEqualTo(Locale.US);
    }

    @Test
    void shouldAllowSettingLocaleWhenAllowedListUsesLanguageTagFormat() {
        // given
        ConstrainedSessionLocaleResolver constrainedSessionLocaleResolver = new ConstrainedSessionLocaleResolver("hr", List.of("en-US", "hr"));
        MockHttpServletRequest request = createRequest();

        // when
        constrainedSessionLocaleResolver.setLocale(request, new MockHttpServletResponse(), Locale.US);

        // then
        assertThat(request.getSession().getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME)).isEqualTo(Locale.US);
    }

    @Test
    void shouldResolveDefaultLocaleWhenAcceptLanguageLocaleIsNotSupported() {
        // given
        ConstrainedSessionLocaleResolver constrainedSessionLocaleResolver = new ConstrainedSessionLocaleResolver("hr", List.of("hr", "en"));
        MockHttpServletRequest request = createRequestWithPreferredLocale(Locale.JAPANESE);

        // when
        Locale resolvedLocale = constrainedSessionLocaleResolver.resolveLocale(request);

        // then
        assertThat(resolvedLocale).isEqualTo(Locale.forLanguageTag("hr"));
    }

    @Test
    void shouldResolveAcceptLanguageLocaleWhenSupported() {
        // given
        ConstrainedSessionLocaleResolver constrainedSessionLocaleResolver = new ConstrainedSessionLocaleResolver("hr", List.of("hr", "en"));
        MockHttpServletRequest request = createRequestWithPreferredLocale(Locale.ENGLISH);

        // when
        Locale resolvedLocale = constrainedSessionLocaleResolver.resolveLocale(request);

        // then
        assertThat(resolvedLocale).isEqualTo(Locale.ENGLISH);
    }

    @Test
    void shouldConstrainLocaleSetThroughSetLocaleContext() {
        // given
        ConstrainedSessionLocaleResolver constrainedSessionLocaleResolver = new ConstrainedSessionLocaleResolver("hr", List.of("hr", "en"));
        MockHttpServletRequest request = createRequest();

        // when
        constrainedSessionLocaleResolver.setLocaleContext(request, new MockHttpServletResponse(), new SimpleLocaleContext(Locale.JAPANESE));

        // then
        assertThat(request.getSession().getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME)).isEqualTo(Locale.forLanguageTag("hr"));
    }

    @Test
    void shouldPreserveExistingTimeZoneWhenSettingLocale() {
        // given
        ConstrainedSessionLocaleResolver constrainedSessionLocaleResolver = new ConstrainedSessionLocaleResolver("hr", List.of("hr", "en"));
        TimeZone timeZone = TimeZone.getTimeZone("Europe/Zagreb");
        MockHttpServletRequest request = createRequestWithTimeZone(timeZone);

        // when
        constrainedSessionLocaleResolver.setLocale(request, new MockHttpServletResponse(), Locale.ENGLISH);

        // then
        assertThat(request.getSession().getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME)).isEqualTo(Locale.ENGLISH);
        assertThat(request.getSession().getAttribute(SessionLocaleResolver.TIME_ZONE_SESSION_ATTRIBUTE_NAME)).isEqualTo(timeZone);
    }

    @Test
    void shouldPreserveTimeZoneWhenConstrainingUnsupportedLocaleContext() {
        // given
        ConstrainedSessionLocaleResolver constrainedSessionLocaleResolver = new ConstrainedSessionLocaleResolver("hr", List.of("hr", "en"));
        TimeZone timeZone = TimeZone.getTimeZone("Europe/Zagreb");
        MockHttpServletRequest request = createRequest();

        // when
        constrainedSessionLocaleResolver.setLocaleContext(request, new MockHttpServletResponse(), new SimpleTimeZoneAwareLocaleContext(Locale.JAPANESE, timeZone));

        // then
        assertThat(request.getSession().getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME)).isEqualTo(Locale.forLanguageTag("hr"));
        assertThat(request.getSession().getAttribute(SessionLocaleResolver.TIME_ZONE_SESSION_ATTRIBUTE_NAME)).isEqualTo(timeZone);
    }
}
