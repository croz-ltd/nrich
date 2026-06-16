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

package net.croz.nrich.webmvc.localeresolver.testutil;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;

import java.util.Locale;
import java.util.TimeZone;

public final class LocaleResolverRequestGeneratingUtil {

    private LocaleResolverRequestGeneratingUtil() {
    }

    public static MockHttpServletRequest createRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setSession(new MockHttpSession());

        return request;
    }

    public static MockHttpServletRequest createRequestWithPreferredLocale(Locale preferredLocale) {
        MockHttpServletRequest request = createRequest();

        request.addPreferredLocale(preferredLocale);

        return request;
    }

    public static MockHttpServletRequest createRequestWithTimeZone(TimeZone timeZone) {
        MockHttpServletRequest request = createRequest();

        WebUtils.setSessionAttribute(request, SessionLocaleResolver.TIME_ZONE_SESSION_ATTRIBUTE_NAME, timeZone);

        return request;
    }
}
