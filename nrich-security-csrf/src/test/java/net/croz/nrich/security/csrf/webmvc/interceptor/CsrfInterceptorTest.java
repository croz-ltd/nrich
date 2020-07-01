package net.croz.nrich.security.csrf.webmvc.interceptor;

import net.croz.nrich.security.csrf.api.service.CsrfTokenManagerService;
import net.croz.nrich.security.csrf.core.constants.CsrfConstants;
import net.croz.nrich.security.csrf.core.exception.CsrfTokenException;
import net.croz.nrich.security.csrf.core.service.AesCsrfTokenManagerService;
import net.croz.nrich.security.csrf.core.service.stub.CsrfTestController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.time.Duration;
import java.util.Arrays;

import static net.croz.nrich.security.csrf.core.testutil.CsrfCoreGeneratingUtil.csrfExcludeConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class CsrfInterceptorTest {

    private CsrfInterceptor csrfInterceptor;

    private static final String CSRF_TOKEN_KEY_NAME = "X-CSRF-Token";

    private static final String CSRF_PING_URL = "/csrf/ping";

    private static final String CSRF_INITIAL_TOKEN_URL = "/csrf/initial/token";

    private static final String CSRF_EXCLUDED_URI = "/excluded/uri";

    private static final String CSRF_SECURED_ENDPOINT = "/secured/url";

    @BeforeEach
    void setup() {
        final CsrfTokenManagerService csrfTokenManagerService = new AesCsrfTokenManagerService(Duration.ofMinutes(35), Duration.ofMinutes(1), CSRF_TOKEN_KEY_NAME, 128);

        csrfInterceptor = new CsrfInterceptor(csrfTokenManagerService, CSRF_INITIAL_TOKEN_URL, CSRF_PING_URL, Arrays.asList(csrfExcludeConfig(CSRF_EXCLUDED_URI, null), csrfExcludeConfig(CSRF_INITIAL_TOKEN_URL, null)));
    }

    @Test
    void shouldPassThroughRequestWithoutPath() {
        // given
        final MockHttpServletRequest emptyRequest = MockMvcRequestBuilders.post(CsrfConstants.EMPTY_PATH).buildRequest(new MockServletContext());

        // when
        final boolean result = csrfInterceptor.preHandle(emptyRequest, new MockHttpServletResponse(), new Object());

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldPassThroughRequestForResource() {
        // given
        final MockHttpServletRequest emptyRequest = MockMvcRequestBuilders.post("/css/style.css").buildRequest(new MockServletContext());

        // when
        final boolean result = csrfInterceptor.preHandle(emptyRequest, new MockHttpServletResponse(), new ResourceHttpRequestHandler());

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldUpdateLastRealApiCallForExcludedUrl() {
        // given
        final MockHttpServletRequest excludedRequest = MockMvcRequestBuilders.post(CSRF_EXCLUDED_URI).session(new MockHttpSession()).buildRequest(new MockServletContext());

        // when
        final boolean result = csrfInterceptor.preHandle(excludedRequest, new MockHttpServletResponse(), new Object());

        // then
        assertThat(result).isTrue();
        assertThat(excludedRequest.getSession()).isNotNull();
        assertThat(excludedRequest.getSession().getAttribute(CsrfConstants.NRICH_LAST_REAL_API_REQUEST_MILLIS)).isNotNull();
    }

    @Test
    void shouldInvalidateSessionWhenMaxInactiveTimeIsReachedForPingUri() {
        // given
        final MockHttpSession session = new MockHttpSession();

        session.setMaxInactiveInterval(10);
        session.setAttribute(CsrfConstants.NRICH_LAST_REAL_API_REQUEST_MILLIS, 0L);

        final MockHttpServletRequest pingRequest = MockMvcRequestBuilders.post(CSRF_PING_URL).session(session).buildRequest(new MockServletContext());

        // when
        final boolean result = csrfInterceptor.preHandle(pingRequest, new MockHttpServletResponse(), new Object());

        // then
        assertThat(result).isTrue();
        assertThat(session.isInvalid()).isTrue();
    }

    @Test
    void shouldReturnInitialTokenWhenAccessingInitialTokenUrl() {
        // given
        final ModelAndView modelAndView = new ModelAndView();
        final MockHttpServletRequest initialTokenRequest = MockMvcRequestBuilders.post(CSRF_INITIAL_TOKEN_URL).session(new MockHttpSession()).buildRequest(new MockServletContext());

        // when
        csrfInterceptor.postHandle(initialTokenRequest, new MockHttpServletResponse(), new Object(), modelAndView);

        // then
        assertThat(modelAndView.getModel().get(CsrfConstants.CSRF_INITIAL_TOKEN_ATTRIBUTE_NAME)).isNotNull();
    }

    @Test
    void shouldReturnErrorWhenTokenDoesntExist() {
        // given
        final MockHttpServletRequest securedUrlRequest = MockMvcRequestBuilders.post(CSRF_SECURED_ENDPOINT).session(new MockHttpSession()).buildRequest(new MockServletContext());

        // when
        final Throwable thrown = catchThrowable(() -> csrfInterceptor.preHandle(securedUrlRequest, new MockHttpServletResponse(), new Object()));

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown).isInstanceOf(CsrfTokenException.class);
    }

    @Test
    void shouldReturnSuccessWhenTokenExistsInHeader() {
        // given
        final MockHttpSession session = new MockHttpSession();
        final ModelAndView modelAndView = new ModelAndView();
        final MockHttpServletRequest initialTokenRequest = MockMvcRequestBuilders.post(CSRF_INITIAL_TOKEN_URL).session(session).buildRequest(new MockServletContext());

        csrfInterceptor.postHandle(initialTokenRequest, new MockHttpServletResponse(), new Object(), modelAndView);

        final String csrfToken = (String) modelAndView.getModel().get(CsrfConstants.CSRF_INITIAL_TOKEN_ATTRIBUTE_NAME);

        final MockHttpServletRequest securedUrlRequest = MockMvcRequestBuilders.post(CSRF_SECURED_ENDPOINT).session(session).header(CSRF_TOKEN_KEY_NAME, csrfToken).buildRequest(new MockServletContext());

        // when
        final boolean result = csrfInterceptor.preHandle(securedUrlRequest, new MockHttpServletResponse(), new Object());

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnSuccessWhenCsrfTokenExistsInParameters() {
        // given
        final MockHttpSession session = new MockHttpSession();
        final ModelAndView modelAndView = new ModelAndView();
        final MockHttpServletRequest initialTokenRequest = MockMvcRequestBuilders.post(CSRF_INITIAL_TOKEN_URL).session(session).buildRequest(new MockServletContext());

        csrfInterceptor.postHandle(initialTokenRequest, new MockHttpServletResponse(), new Object(), modelAndView);

        final String csrfToken = (String) modelAndView.getModel().get(CsrfConstants.CSRF_INITIAL_TOKEN_ATTRIBUTE_NAME);

        final MockHttpServletRequest securedUrlRequest = MockMvcRequestBuilders.post(CSRF_SECURED_ENDPOINT).session(session).param(CSRF_TOKEN_KEY_NAME, csrfToken).buildRequest(new MockServletContext());

        // when
        final boolean result = csrfInterceptor.preHandle(securedUrlRequest, new MockHttpServletResponse(), new Object());

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnResultWhenAccessingResourceWithToken() throws Exception {
        // given
        final MockHttpSession session = new MockHttpSession();
        final ModelAndView modelAndView = new ModelAndView();
        final MockHttpServletRequest initialTokenRequest = MockMvcRequestBuilders.post(CSRF_INITIAL_TOKEN_URL).session(session).buildRequest(new MockServletContext());

        csrfInterceptor.postHandle(initialTokenRequest, new MockHttpServletResponse(), new Object(), modelAndView);

        final String csrfToken = (String) modelAndView.getModel().get(CsrfConstants.CSRF_INITIAL_TOKEN_ATTRIBUTE_NAME);

        final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new CsrfTestController()).addInterceptors(csrfInterceptor).build();

        // when
        final MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post(CSRF_SECURED_ENDPOINT)
                .session(session)
                .header(CSRF_TOKEN_KEY_NAME, csrfToken)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("result");
    }
}
