package net.croz.nrich.security.csrf.webflux.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.croz.nrich.security.csrf.api.service.CsrfTokenManagerService;
import net.croz.nrich.security.csrf.core.constants.CsrfConstants;
import net.croz.nrich.security.csrf.core.exception.CsrfTokenException;
import net.croz.nrich.security.csrf.core.model.CsrfExcludeConfig;
import net.croz.nrich.security.csrf.core.util.CsrfUriUtil;
import net.croz.nrich.security.csrf.webflux.holder.WebFluxCsrfTokenHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CsrfWebFilter implements WebFilter {

    private final CsrfTokenManagerService csrfTokenManagerService;

    private final String initialTokenUrl;

    private final String csrfPingUrl;

    private final List<CsrfExcludeConfig> csrfExcludeConfigList;

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final WebFilterChain chain) {
        log.debug("csrfFilter.filter()");

        final String pathWithinApplication = exchange.getRequest().getPath().pathWithinApplication().value();

        final Mono<Void> result = chain.filter(exchange);
        if (CsrfConstants.EMPTY_PATH.equals(pathWithinApplication)) {
            return result;
        }

        return exchange.getSession().flatMap(webSession -> {
            Mono<Void> csrfActionResult = result;
            final String requestUri = uri(exchange);

            if (CsrfUriUtil.excludeUri(csrfExcludeConfigList, requestUri)) {

                updateLastApiCallAttribute(webSession);

            }
            else if (requestUri.endsWith(csrfPingUrl)) {

                csrfActionResult = handleCsrfPingUrl(exchange, webSession).flatMap(value -> result);

            }
            else if (webSession != null) {

                csrfTokenManagerService.validateAndRefreshToken(new WebFluxCsrfTokenHolder(exchange, webSession));

                updateLastApiCallAttribute(webSession);
            }
            else {
                // Session doesn't exists, but we should not pass through request.
                return Mono.error(new CsrfTokenException("Can't validate token. There is no session."));
            }

            return csrfActionResult.doOnSuccess(value -> addInitialToken(exchange, webSession));
        });
    }

    private void addInitialToken(final ServerWebExchange exchange, final WebSession webSession) {

        if (uri(exchange).endsWith(initialTokenUrl)) {

            exchange.getAttributes().put(CsrfConstants.CSRF_INITIAL_TOKEN_ATTRIBUTE_NAME, csrfTokenManagerService.generateToken(new WebFluxCsrfTokenHolder(exchange, webSession)));

            updateLastApiCallAttribute(webSession);
        }
    }

    private Mono<Void> handleCsrfPingUrl(final ServerWebExchange exchange, final WebSession webSession) {
        if (webSession != null) {
            final Long lastRealApiRequestMillis = webSession.getAttribute(CsrfConstants.NRICH_LAST_REAL_API_REQUEST_MILLIS);
            log.debug("    lastRealApiRequestMillis: {}", lastRealApiRequestMillis);

            if (lastRealApiRequestMillis != null) {
                final long deltaMillis = System.currentTimeMillis() - lastRealApiRequestMillis;
                log.debug("    deltaMillis: {}", deltaMillis);

                final long maxInactiveIntervalMillis = webSession.getMaxIdleTime().toMillis();
                log.debug("    maxInactiveIntervalMillis: {}", maxInactiveIntervalMillis);

                if ((maxInactiveIntervalMillis > 0) && (deltaMillis > maxInactiveIntervalMillis)) {
                    return webSession.invalidate().doOnSuccess(value -> {
                        log.debug("    sessionJustInvalidated");

                        exchange.getResponse().getHeaders().add(CsrfConstants.CSRF_PING_STOP_HEADER_NAME, "stopPing");

                        log.debug("    sending csrf stop ping header in response");

                        updateLastActiveRequestMillis(exchange, 0L);

                        updateLastActiveRequestMillis(exchange, deltaMillis);
                    });
                }
            }
        }

        updateLastActiveRequestMillis(exchange, 0L);

        return Mono.fromRunnable(() -> csrfTokenManagerService.validateAndRefreshToken(new WebFluxCsrfTokenHolder(exchange, webSession)));
    }

    private void updateLastApiCallAttribute(final WebSession webSession) {
        if (webSession != null) {
            webSession.getAttributes().put(CsrfConstants.NRICH_LAST_REAL_API_REQUEST_MILLIS, System.currentTimeMillis());
        }
    }

    private String uri(final ServerWebExchange exchange) {
        return exchange.getRequest().getURI().toString();
    }

    private void updateLastActiveRequestMillis(final ServerWebExchange exchange, final long deltaMillis) {
        exchange.getResponse().getHeaders().add(CsrfConstants.CSRF_AFTER_LAST_ACTIVE_REQUEST_MILLIS_HEADER_NAME, Long.valueOf(deltaMillis).toString());
    }
}
