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

package net.croz.nrich.security.csrf.webflux.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.croz.nrich.security.csrf.api.service.CsrfTokenManagerService;
import net.croz.nrich.security.csrf.core.constants.CsrfConstants;
import net.croz.nrich.security.csrf.core.exception.CsrfTokenException;
import net.croz.nrich.security.csrf.core.model.CsrfExcludeConfig;
import net.croz.nrich.security.csrf.core.util.CsrfUriUtil;
import net.croz.nrich.security.csrf.webflux.holder.WebFluxCsrfTokenKeyHolder;
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

    private final String tokenKeyName;

    private final String initialTokenUrl;

    private final String csrfPingUrl;

    private final List<CsrfExcludeConfig> csrfExcludeConfigList;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.debug("csrfFilter.filter()");

        String pathWithinApplication = exchange.getRequest().getPath().pathWithinApplication().value();

        Mono<Void> result = chain.filter(exchange);
        if (CsrfConstants.EMPTY_PATH.equals(pathWithinApplication)) {
            return result;
        }

        String requestUri = uri(exchange);
        return exchange.getSession().switchIfEmpty(Mono.defer(() -> returnErrorIfCsrfProtectedUri(requestUri))).flatMap(webSession -> {
            Mono<Void> csrfActionResult = result;

            if (CsrfUriUtil.excludeUri(csrfExcludeConfigList, requestUri)) {
                updateLastApiCallAttribute(webSession);
            }
            else if (requestUri.endsWith(csrfPingUrl)) {
                csrfActionResult = handleCsrfPingUrl(exchange, webSession).flatMap(value -> result);
            }
            else {
                csrfTokenManagerService.validateAndRefreshToken(new WebFluxCsrfTokenKeyHolder(exchange, webSession, tokenKeyName, CsrfConstants.CSRF_CRYPTO_KEY_NAME));

                updateLastApiCallAttribute(webSession);
            }

            return csrfActionResult.doOnSuccess(value -> addInitialToken(exchange, webSession));
        });
    }

    private void addInitialToken(ServerWebExchange exchange, WebSession webSession) {
        if (uri(exchange).endsWith(initialTokenUrl)) {
            String token = csrfTokenManagerService.generateToken(new WebFluxCsrfTokenKeyHolder(exchange, webSession, tokenKeyName, CsrfConstants.CSRF_CRYPTO_KEY_NAME));

            exchange.getAttributes().put(CsrfConstants.CSRF_INITIAL_TOKEN_ATTRIBUTE_NAME, token);

            updateLastApiCallAttribute(webSession);
        }
    }

    private Mono<Void> handleCsrfPingUrl(ServerWebExchange exchange, WebSession webSession) {
        Long lastRealApiRequestMillis = webSession.getAttribute(CsrfConstants.NRICH_LAST_REAL_API_REQUEST_MILLIS);
        log.debug("    lastRealApiRequestMillis: {}", lastRealApiRequestMillis);

        if (lastRealApiRequestMillis != null) {
            long deltaMillis = System.currentTimeMillis() - lastRealApiRequestMillis;
            log.debug("    deltaMillis: {}", deltaMillis);

            long maxInactiveIntervalMillis = webSession.getMaxIdleTime().toMillis();
            log.debug("    maxInactiveIntervalMillis: {}", maxInactiveIntervalMillis);

            if ((maxInactiveIntervalMillis > 0) && (deltaMillis > maxInactiveIntervalMillis)) {
                return webSession.invalidate().doOnSuccess(value -> {
                    log.debug("    sessionJustInvalidated");

                    exchange.getResponse().getHeaders().add(CsrfConstants.CSRF_PING_STOP_HEADER_NAME, "stopPing");

                    log.debug("    sending csrf stop ping header in response");

                    updateLastActiveRequestMillis(exchange, deltaMillis);
                });
            }
        }

        updateLastActiveRequestMillis(exchange, 0L);

        return Mono.fromRunnable(() -> csrfTokenManagerService.validateAndRefreshToken(new WebFluxCsrfTokenKeyHolder(exchange, webSession, tokenKeyName, CsrfConstants.CSRF_CRYPTO_KEY_NAME)));
    }

    private void updateLastApiCallAttribute(WebSession webSession) {
        webSession.getAttributes().put(CsrfConstants.NRICH_LAST_REAL_API_REQUEST_MILLIS, System.currentTimeMillis());
    }

    private String uri(ServerWebExchange exchange) {
        return exchange.getRequest().getURI().toString();
    }

    private void updateLastActiveRequestMillis(ServerWebExchange exchange, long deltaMillis) {
        exchange.getResponse().getHeaders().add(CsrfConstants.CSRF_AFTER_LAST_ACTIVE_REQUEST_MILLIS_HEADER_NAME, Long.toString(deltaMillis));
    }

    private Mono<WebSession> returnErrorIfCsrfProtectedUri(String requestUri) {
        if (CsrfUriUtil.excludeUri(csrfExcludeConfigList, requestUri) || requestUri.endsWith(csrfPingUrl)) {
            return Mono.empty();
        }

        // Session doesn't exist, but we should not pass through request.
        return Mono.error(new CsrfTokenException("Can't validate token. There is no session."));
    }
}
