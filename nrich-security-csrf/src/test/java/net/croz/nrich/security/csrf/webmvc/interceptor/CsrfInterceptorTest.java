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

package net.croz.nrich.security.csrf.webmvc.interceptor;

import net.croz.nrich.security.csrf.api.service.CsrfTokenManagerService;
import net.croz.nrich.security.csrf.core.constants.CsrfConstants;
import net.croz.nrich.security.csrf.core.exception.CsrfTokenException;
import net.croz.nrich.security.csrf.core.model.CsrfExcludeConfig;
import net.croz.nrich.security.csrf.core.service.AesCsrfTokenManagerService;
import net.croz.nrich.security.csrf.core.service.stub.CsrfTestController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static net.croz.nrich.security.csrf.core.testutil.CsrfCoreGeneratingUtil.csrfExcludeConfig;
import static net.croz.nrich.security.csrf.webmvc.testutil.CsrfInterceptorGeneratingUtil.createHttpServletRequest;
import static net.croz.nrich.security.csrf.webmvc.testutil.CsrfInterceptorGeneratingUtil.createHttpServletRequestWithHeader;
import static net.croz.nrich.security.csrf.webmvc.testutil.CsrfInterceptorGeneratingUtil.createHttpServletRequestWithParam;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CsrfInterceptorTest {

    private static final String CSRF_TOKEN_KEY_NAME = "X-CSRF-Token";

    private static final String CSRF_PING_URL = "/csrf/ping";

    private static final String CSRF_INITIAL_TOKEN_URL = "/csrf/initial/token";

    private static final String CSRF_EXCLUDED_URI = "/excluded/uri";

    private static final String CSRF_SECURED_ENDPOINT = "/secured/url";

    private CsrfInterceptor csrfInterceptor;

    @BeforeEach
    void setup() {
        CsrfTokenManagerService csrfTokenManagerService = new AesCsrfTokenManagerService(Duration.ofMinutes(35), Duration.ofMinutes(1), 128);
        List<CsrfExcludeConfig> csrfExcludeConfigList = Arrays.asList(csrfExcludeConfig(CSRF_EXCLUDED_URI, null), csrfExcludeConfig(CSRF_INITIAL_TOKEN_URL, null));

        csrfInterceptor = new CsrfInterceptor(csrfTokenManagerService, CSRF_TOKEN_KEY_NAME, CSRF_INITIAL_TOKEN_URL, CSRF_PING_URL, csrfExcludeConfigList);
    }

    @Test
    void shouldPassThroughRequestWithoutPath() {
        // given
        HttpServletRequest emptyRequest = createHttpServletRequest(CsrfConstants.EMPTY_PATH);

        // when
        boolean result = csrfInterceptor.preHandle(emptyRequest, new MockHttpServletResponse(), new Object());

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldPassThroughRequestForResource() {
        // given
        HttpServletRequest emptyRequest = createHttpServletRequest("/css/style.css");

        // when
        boolean result = csrfInterceptor.preHandle(emptyRequest, new MockHttpServletResponse(), new ResourceHttpRequestHandler());

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldUpdateLastRealApiCallForExcludedUrl() {
        // given
        HttpServletRequest excludedRequest = createHttpServletRequest(CSRF_EXCLUDED_URI);

        // when
        boolean result = csrfInterceptor.preHandle(excludedRequest, new MockHttpServletResponse(), new Object());

        // then
        assertThat(result).isTrue();
        assertThat(excludedRequest.getSession()).isNotNull();
        assertThat(excludedRequest.getSession().getAttribute(CsrfConstants.NRICH_LAST_REAL_API_REQUEST_MILLIS)).isNotNull();
    }

    @Test
    void shouldInvalidateSessionWhenMaxInactiveTimeIsReachedForPingUri() {
        // given
        MockHttpSession session = new MockHttpSession();

        session.setMaxInactiveInterval(10);
        session.setAttribute(CsrfConstants.NRICH_LAST_REAL_API_REQUEST_MILLIS, 0L);

        HttpServletRequest pingRequest = createHttpServletRequest(CSRF_PING_URL, session);

        // when
        boolean result = csrfInterceptor.preHandle(pingRequest, new MockHttpServletResponse(), new Object());

        // then
        assertThat(result).isTrue();
        assertThat(session.isInvalid()).isTrue();
    }

    @Test
    void shouldReturnInitialTokenWhenAccessingInitialTokenUrl() {
        // given
        ModelAndView modelAndView = new ModelAndView();
        HttpServletRequest initialTokenRequest = createHttpServletRequest(CSRF_INITIAL_TOKEN_URL, new MockHttpSession());

        // when
        csrfInterceptor.postHandle(initialTokenRequest, new MockHttpServletResponse(), new Object(), modelAndView);

        // then
        assertThat(modelAndView.getModel().get(CsrfConstants.CSRF_INITIAL_TOKEN_ATTRIBUTE_NAME)).isNotNull();
    }

    @Test
    void shouldReturnErrorWhenTokenDoesntExist() {
        // given
        HttpServletRequest securedUrlRequest = createHttpServletRequest(CSRF_SECURED_ENDPOINT);

        // when
        Throwable thrown = catchThrowable(() -> csrfInterceptor.preHandle(securedUrlRequest, new MockHttpServletResponse(), new Object()));

        // then
        assertThat(thrown).isInstanceOf(CsrfTokenException.class).hasMessage("Csrf token is not available!");
    }

    @Test
    void shouldThrowExceptionWhenSessionDoesntExist() {
        // given
        HttpServletRequest securedUrlRequest = createHttpServletRequest(CSRF_SECURED_ENDPOINT, null);

        // when
        Throwable thrown = catchThrowable(() -> csrfInterceptor.preHandle(securedUrlRequest, new MockHttpServletResponse(), new Object()));

        // then
        assertThat(thrown).isInstanceOf(CsrfTokenException.class).hasMessage("Can't validate token. There is no session.");
    }

    @ValueSource(strings = { CSRF_SECURED_ENDPOINT, CSRF_PING_URL })
    @ParameterizedTest
    void shouldReturnSuccessWhenTokenExistsInHeader(String uri) {
        // given
        MockHttpSession session = new MockHttpSession();
        String csrfToken = generateCsrfToken(session);
        HttpServletRequest securedUrlRequest = createHttpServletRequestWithHeader(uri, session, CSRF_TOKEN_KEY_NAME, csrfToken);

        // when
        boolean result = csrfInterceptor.preHandle(securedUrlRequest, new MockHttpServletResponse(), new Object());

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnSuccessWhenCsrfTokenExistsInParameters() {
        // given
        MockHttpSession session = new MockHttpSession();
        ModelAndView modelAndView = new ModelAndView();
        HttpServletRequest initialTokenRequest = createHttpServletRequest(CSRF_INITIAL_TOKEN_URL, session);

        csrfInterceptor.postHandle(initialTokenRequest, new MockHttpServletResponse(), new Object(), modelAndView);

        String csrfToken = (String) modelAndView.getModel().get(CsrfConstants.CSRF_INITIAL_TOKEN_ATTRIBUTE_NAME);

        HttpServletRequest securedUrlRequest = createHttpServletRequestWithParam(CSRF_SECURED_ENDPOINT, session, CSRF_TOKEN_KEY_NAME, csrfToken);

        // when
        boolean result = csrfInterceptor.preHandle(securedUrlRequest, new MockHttpServletResponse(), new Object());

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnResultWhenAccessingResourceWithToken() throws Exception {
        // given
        MockHttpSession session = new MockHttpSession();
        String csrfToken = generateCsrfToken(session);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new CsrfTestController()).addInterceptors(csrfInterceptor).build();

        // when
        ResultActions result = mockMvc.perform(post(CSRF_SECURED_ENDPOINT)
            .session(session)
            .header(CSRF_TOKEN_KEY_NAME, csrfToken));

        // then
        result.andExpect(status().isOk())
            .andExpect(content().string("result"));
    }

    private String generateCsrfToken(MockHttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        HttpServletRequest initialTokenRequest = createHttpServletRequest(CSRF_INITIAL_TOKEN_URL, session);

        csrfInterceptor.postHandle(initialTokenRequest, new MockHttpServletResponse(), new Object(), modelAndView);

        return (String) modelAndView.getModel().get(CsrfConstants.CSRF_INITIAL_TOKEN_ATTRIBUTE_NAME);
    }
}
