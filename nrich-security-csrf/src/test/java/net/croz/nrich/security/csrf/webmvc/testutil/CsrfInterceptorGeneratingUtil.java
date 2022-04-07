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
