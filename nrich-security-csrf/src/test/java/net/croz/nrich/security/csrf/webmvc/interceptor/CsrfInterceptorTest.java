package net.croz.nrich.security.csrf.webmvc.interceptor;

import net.croz.nrich.security.csrf.api.service.CsrfTokenManagerService;
import net.croz.nrich.security.csrf.core.constants.CsrfConstants;
import net.croz.nrich.security.csrf.core.exception.CsrfTokenException;
import net.croz.nrich.security.csrf.core.service.AesCsrfTokenManagerService;
import net.croz.nrich.security.csrf.core.service.stub.CsrfTestController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Arrays;

import static net.croz.nrich.security.csrf.core.testutil.CsrfCoreGeneratingUtil.csrfExcludeConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

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

        csrfInterceptor = new CsrfInterceptor(csrfTokenManagerService, CSRF_TOKEN_KEY_NAME, CSRF_INITIAL_TOKEN_URL, CSRF_PING_URL, Arrays.asList(csrfExcludeConfig(CSRF_EXCLUDED_URI, null), csrfExcludeConfig(CSRF_INITIAL_TOKEN_URL, null)));
    }

    @Test
    void shouldPassThroughRequestWithoutPath() {
        // given
        HttpServletRequest emptyRequest = MockMvcRequestBuilders.post(CsrfConstants.EMPTY_PATH).buildRequest(new MockServletContext());

        // when
        boolean result = csrfInterceptor.preHandle(emptyRequest, new MockHttpServletResponse(), new Object());

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldPassThroughRequestForResource() {
        // given
        HttpServletRequest emptyRequest = MockMvcRequestBuilders.post("/css/style.css").buildRequest(new MockServletContext());

        // when
        boolean result = csrfInterceptor.preHandle(emptyRequest, new MockHttpServletResponse(), new ResourceHttpRequestHandler());

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldUpdateLastRealApiCallForExcludedUrl() {
        // given
        HttpServletRequest excludedRequest = MockMvcRequestBuilders.post(CSRF_EXCLUDED_URI).session(new MockHttpSession()).buildRequest(new MockServletContext());

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

        HttpServletRequest pingRequest = MockMvcRequestBuilders.post(CSRF_PING_URL).session(session).buildRequest(new MockServletContext());

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
        HttpServletRequest initialTokenRequest = MockMvcRequestBuilders.post(CSRF_INITIAL_TOKEN_URL).session(new MockHttpSession()).buildRequest(new MockServletContext());

        // when
        csrfInterceptor.postHandle(initialTokenRequest, new MockHttpServletResponse(), new Object(), modelAndView);

        // then
        assertThat(modelAndView.getModel().get(CsrfConstants.CSRF_INITIAL_TOKEN_ATTRIBUTE_NAME)).isNotNull();
    }

    @Test
    void shouldReturnErrorWhenTokenDoesntExist() {
        // given
        HttpServletRequest securedUrlRequest = MockMvcRequestBuilders.post(CSRF_SECURED_ENDPOINT).session(new MockHttpSession()).buildRequest(new MockServletContext());

        // when
        Throwable thrown = catchThrowable(() -> csrfInterceptor.preHandle(securedUrlRequest, new MockHttpServletResponse(), new Object()));

        // then
        assertThat(thrown).isInstanceOf(CsrfTokenException.class).hasMessage("Csrf token is not available!");
    }

    @Test
    void shouldThrowExceptionWhenSessionDoesntExist() {
        // given
        HttpServletRequest securedUrlRequest = MockMvcRequestBuilders.post(CSRF_SECURED_ENDPOINT).buildRequest(new MockServletContext());

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
        HttpServletRequest securedUrlRequest = MockMvcRequestBuilders.post(uri).session(session).header(CSRF_TOKEN_KEY_NAME, csrfToken).buildRequest(new MockServletContext());

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
        HttpServletRequest initialTokenRequest = MockMvcRequestBuilders.post(CSRF_INITIAL_TOKEN_URL).session(session).buildRequest(new MockServletContext());

        csrfInterceptor.postHandle(initialTokenRequest, new MockHttpServletResponse(), new Object(), modelAndView);

        String csrfToken = (String) modelAndView.getModel().get(CsrfConstants.CSRF_INITIAL_TOKEN_ATTRIBUTE_NAME);

        MockHttpServletRequest securedUrlRequest = MockMvcRequestBuilders.post(CSRF_SECURED_ENDPOINT).session(session).param(CSRF_TOKEN_KEY_NAME, csrfToken).buildRequest(new MockServletContext());

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
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post(CSRF_SECURED_ENDPOINT)
                .session(session)
                .header(CSRF_TOKEN_KEY_NAME, csrfToken)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("result");
    }

    private String generateCsrfToken(MockHttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        HttpServletRequest initialTokenRequest = MockMvcRequestBuilders.post(CSRF_INITIAL_TOKEN_URL).session(session).buildRequest(new MockServletContext());

        csrfInterceptor.postHandle(initialTokenRequest, new MockHttpServletResponse(), new Object(), modelAndView);

        return (String) modelAndView.getModel().get(CsrfConstants.CSRF_INITIAL_TOKEN_ATTRIBUTE_NAME);
    }
}
