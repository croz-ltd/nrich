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

package net.croz.nrich.security.csrf.webmvc.testutil;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public final class CsrfInterceptorGeneratingUtil {

    private CsrfInterceptorGeneratingUtil() {
    }

    public static HttpServletRequest createHttpServletRequest(String url) {
        return createHttpServletRequest(url, new MockHttpSession());
    }

    public static HttpServletRequest createHttpServletRequest(String url, MockHttpSession mockHttpSession) {
        return createHttpServletRequest(url, mockHttpSession, null, null, null, null);
    }

    public static HttpServletRequest createHttpServletRequestWithParam(String url, MockHttpSession mockHttpSession, String key, String value) {
        return createHttpServletRequest(url, mockHttpSession, key, value, null, null);
    }

    public static HttpServletRequest createHttpServletRequestWithHeader(String url, MockHttpSession mockHttpSession, String key, String value) {
        return createHttpServletRequest(url, mockHttpSession, null, null, key, value);
    }

    private static HttpServletRequest createHttpServletRequest(String url, MockHttpSession mockHttpSession, String paramKey, String paramValue, String headerKey, String headerValue) {
        MockHttpServletRequestBuilder builder = post(url);

        if (mockHttpSession != null) {
            builder = builder.session(mockHttpSession);
        }

        if (paramKey != null) {
            builder = builder.param(paramKey, paramValue);
        }

        if (headerKey != null) {
            builder = builder.header(headerKey, headerValue);
        }

        return builder.buildRequest(new MockServletContext());
    }
}
