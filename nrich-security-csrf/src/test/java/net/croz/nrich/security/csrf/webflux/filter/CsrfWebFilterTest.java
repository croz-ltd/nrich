package net.croz.nrich.security.csrf.webflux.filter;

import net.croz.nrich.security.csrf.api.service.CsrfTokenManagerService;
import net.croz.nrich.security.csrf.core.constants.CsrfConstants;
import net.croz.nrich.security.csrf.core.exception.CsrfTokenException;
import net.croz.nrich.security.csrf.core.service.AesCsrfTokenManagerService;
import net.croz.nrich.security.csrf.core.service.stub.CsrfTestController;
import net.croz.nrich.security.csrf.webflux.stub.TestWebSessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.mock.web.server.MockWebSession;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.WebSession;
import org.springframework.web.server.session.WebSessionManager;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;

import static net.croz.nrich.security.csrf.core.testutil.CsrfCoreGeneratingUtil.csrfExcludeConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CsrfWebFilterTest {

    private static final String CSRF_TOKEN_KEY_NAME = "X-CSRF-Token";

    private static final String CSRF_PING_URL = "/csrf/ping";

    private static final String CSRF_INITIAL_TOKEN_URL = "/csrf/initial/token";

    private static final String CSRF_EXCLUDED_URI = "/excluded/uri";

    private static final String CSRF_SECURED_ENDPOINT = "/secured/url";

    private CsrfWebFilter csrfFilter;

    private WebFilterChain chain;

    @BeforeEach
    void setup() {
        CsrfTokenManagerService csrfTokenManagerService = new AesCsrfTokenManagerService(Duration.ofMinutes(35), Duration.ofMinutes(1), 128);

        csrfFilter = new CsrfWebFilter(csrfTokenManagerService, CSRF_TOKEN_KEY_NAME, CSRF_INITIAL_TOKEN_URL, CSRF_PING_URL, Arrays.asList(csrfExcludeConfig(CSRF_EXCLUDED_URI, null), csrfExcludeConfig(CSRF_INITIAL_TOKEN_URL, null)));

        chain = mock(WebFilterChain.class);

        when(chain.filter(any())).thenReturn(Mono.empty());
    }

    @Test
    void shouldPassThroughRequestWithoutPath() {
        // given
        ServerWebExchange emptyRequest = MockServerWebExchange.from(MockServerHttpRequest.post(CsrfConstants.EMPTY_PATH));

        // when
        Mono<Void> result = csrfFilter.filter(emptyRequest, chain);

        // then
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void shouldUpdateLastRealApiCallForExcludedUrl() {
        // given
        ServerWebExchange excludedRequest = MockServerWebExchange.from(MockServerHttpRequest.post(CSRF_EXCLUDED_URI));

        // when
        Mono<Void> result = csrfFilter.filter(excludedRequest, chain);

        // then
        StepVerifier.create(result).verifyComplete();
        assertThat((Long) Objects.requireNonNull(excludedRequest.getSession().block()).getAttribute(CsrfConstants.NRICH_LAST_REAL_API_REQUEST_MILLIS)).isNotNull();
    }

    @Test
    void shouldInvalidateSessionWhenMaxInactiveTimeIsReachedForPingUri() {
        // given
        ServerWebExchange pingRequest = MockServerWebExchange.from(MockServerHttpRequest.post(CSRF_PING_URL));

        Objects.requireNonNull(pingRequest.getSession().block()).getAttributes().put(CsrfConstants.NRICH_LAST_REAL_API_REQUEST_MILLIS, 0L);

        // when
        Mono<Void> result = csrfFilter.filter(pingRequest, chain);

        // then
        StepVerifier.create(result).verifyComplete();
        assertThat(Objects.requireNonNull(pingRequest.getSession().block()).isExpired()).isTrue();
    }

    @Test
    void shouldRefreshTokenForPingRequest() {
        // given
        WebSession webSession = new MockWebSession();
        ServerWebExchange initialTokenUrl = csrfTokenExchange(webSession);
        String csrfToken = initialTokenUrl.getAttribute(CsrfConstants.CSRF_INITIAL_TOKEN_ATTRIBUTE_NAME);

        initialTokenUrl.getAttributes().remove(CsrfConstants.NRICH_LAST_REAL_API_REQUEST_MILLIS);

        ServerWebExchange securedUrl = initialTokenUrl.mutate().request(MockServerHttpRequest.post(CSRF_PING_URL).header(CSRF_TOKEN_KEY_NAME, csrfToken).build()).build();

        // when
        Mono<Void> result = csrfFilter.filter(securedUrl, chain);

        // then
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void shouldReturnInitialTokenWhenAccessingInitialTokenUrl() {
        // given
        ServerWebExchange initialTokenRequest = MockServerWebExchange.from(MockServerHttpRequest.post(CSRF_INITIAL_TOKEN_URL));

        // when
        Mono<Void> result = csrfFilter.filter(initialTokenRequest, chain);

        // then
        StepVerifier.create(result).verifyComplete();
        initialTokenRequest.getAttribute(CsrfConstants.CSRF_INITIAL_TOKEN_ATTRIBUTE_NAME);
    }

    @Test
    void shouldReturnErrorWhenTokenDoesntExist() {
        // given
        ServerWebExchange securedUrlRequest = MockServerWebExchange.from(MockServerHttpRequest.post(CSRF_SECURED_ENDPOINT));

        // when
        Mono<Void> result = csrfFilter.filter(securedUrlRequest, chain);

        // then
        StepVerifier.create(result).verifyError(CsrfTokenException.class);
    }

    @Test
    void shouldReturnErrorWhenSessionDoesntExist() {
        // given
        WebSessionManager webSessionManager = exchange -> Mono.empty();
        ServerWebExchange securedUrlRequest = new MockServerWebExchange.Builder(MockServerHttpRequest.post(CSRF_SECURED_ENDPOINT).build()).sessionManager(webSessionManager).build();

        // when
        Mono<Void> result = csrfFilter.filter(securedUrlRequest, chain);

        // then
        StepVerifier.create(result).verifyError(CsrfTokenException.class);
    }

    @ValueSource(strings = { CSRF_PING_URL, CSRF_EXCLUDED_URI })
    @ParameterizedTest
    void shouldNotReturnErrorWhenSessionDoesntExistForExcludedOrPingUrl(String uri) {
        // given
        WebSessionManager webSessionManager = exchange -> Mono.empty();
        ServerWebExchange securedUrlRequest = new MockServerWebExchange.Builder(MockServerHttpRequest.post(uri).build()).sessionManager(webSessionManager).build();

        // when
        Mono<Void> result = csrfFilter.filter(securedUrlRequest, chain);

        // then
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void shouldReturnSuccessWhenTokenExistsInHeader() {
        // given
        ServerWebExchange initialTokenRequest = csrfTokenExchange(null);
        String csrfToken = initialTokenRequest.getAttribute(CsrfConstants.CSRF_INITIAL_TOKEN_ATTRIBUTE_NAME);

        ServerWebExchange securedUrlRequest = initialTokenRequest.mutate().request(MockServerHttpRequest.post(CSRF_SECURED_ENDPOINT).header(CSRF_TOKEN_KEY_NAME, csrfToken).build()).build();

        // when
        Mono<Void> result = csrfFilter.filter(securedUrlRequest, chain);

        // then
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void shouldReturnSuccessWhenCsrfTokenExistsInParameters() {
        // given
        ServerWebExchange initialTokenUrl = csrfTokenExchange(null);
        String csrfToken = initialTokenUrl.getAttribute(CsrfConstants.CSRF_INITIAL_TOKEN_ATTRIBUTE_NAME);

        ServerWebExchange securedUrl = initialTokenUrl.mutate().request(MockServerHttpRequest.post(CSRF_SECURED_ENDPOINT).queryParam(CSRF_TOKEN_KEY_NAME, csrfToken).build()).build();

        // when
        Mono<Void> result = csrfFilter.filter(securedUrl, chain);

        // then
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void shouldReturnResultWhenAccessingResourceWithToken() {
        // given
        WebSession webSession = new MockWebSession();
        ServerWebExchange initialTokenUrl = csrfTokenExchange(webSession);
        String csrfToken = initialTokenUrl.getAttribute(CsrfConstants.CSRF_INITIAL_TOKEN_ATTRIBUTE_NAME);

        WebTestClient client = WebTestClient.bindToController(new CsrfTestController()).webSessionManager(new TestWebSessionManager(webSession)).webFilter(csrfFilter).build();

        // when
        EntityExchangeResult<byte[]> result = client.post()
            .uri(CSRF_SECURED_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .header(CSRF_TOKEN_KEY_NAME, csrfToken)
            .exchange()
            .expectBody()
            .returnResult();

        // then
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getResponseBody()).isNotNull();
        assertThat(new String(result.getResponseBody(), StandardCharsets.UTF_8)).isEqualTo("result");
    }

    private ServerWebExchange csrfTokenExchange(WebSession webSession) {
        MockServerWebExchange.Builder initialTokenUrlBuilder = MockServerWebExchange.builder(MockServerHttpRequest.post(CSRF_INITIAL_TOKEN_URL));
        MockServerWebExchange initialTokenUrl = webSession == null ? initialTokenUrlBuilder.build() : initialTokenUrlBuilder.session(webSession).build();

        csrfFilter.filter(initialTokenUrl, chain).block();

        return initialTokenUrl;
    }
}
