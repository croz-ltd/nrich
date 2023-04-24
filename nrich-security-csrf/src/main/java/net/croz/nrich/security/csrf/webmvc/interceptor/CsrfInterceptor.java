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

package net.croz.nrich.security.csrf.webmvc.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.croz.nrich.security.csrf.api.service.CsrfTokenManagerService;
import net.croz.nrich.security.csrf.core.constants.CsrfConstants;
import net.croz.nrich.security.csrf.core.exception.CsrfTokenException;
import net.croz.nrich.security.csrf.core.model.CsrfExcludeConfig;
import net.croz.nrich.security.csrf.core.util.CsrfUriUtil;
import net.croz.nrich.security.csrf.webmvc.holder.WebMvcCsrfTokenKeyHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.util.UrlPathHelper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class CsrfInterceptor implements HandlerInterceptor {

    private final CsrfTokenManagerService csrfTokenManagerService;

    private final String tokenKeyName;

    private final String initialTokenUrl;

    private final String csrfPingUrl;

    private final List<CsrfExcludeConfig> csrfExcludeConfigList;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        preHandleInternal(request, response, handler);

        return true;
    }

    protected void preHandleInternal(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.debug("csrfInterceptor.preHandle()");

        if (handler instanceof ResourceHttpRequestHandler) {
            return;
        }

        String pathWithinApplication = new UrlPathHelper().getPathWithinApplication(request);

        if (CsrfConstants.EMPTY_PATH.equals(pathWithinApplication)) {
            return;
        }

        HttpSession httpSession = request.getSession(false);
        String requestUri = request.getRequestURI();

        if (CsrfUriUtil.excludeUri(csrfExcludeConfigList, requestUri)) {
            updateLastApiCallAttribute(httpSession);
        }
        else if (requestUri.endsWith(csrfPingUrl)) {
            handleCsrfPingUrl(request, response, httpSession);
        }
        else if (httpSession != null) {
            csrfTokenManagerService.validateAndRefreshToken(new WebMvcCsrfTokenKeyHolder(request, response, tokenKeyName, CsrfConstants.CSRF_CRYPTO_KEY_NAME));

            updateLastApiCallAttribute(httpSession);
        }
        else {
            // Session doesn't exist, but we should not pass through request.
            throw new CsrfTokenException("Can't validate token. There is no session.");
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (request.getRequestURI().endsWith(initialTokenUrl)) {
            String token = csrfTokenManagerService.generateToken(new WebMvcCsrfTokenKeyHolder(request, response, tokenKeyName, CsrfConstants.CSRF_CRYPTO_KEY_NAME));

            modelAndView.addObject(CsrfConstants.CSRF_INITIAL_TOKEN_ATTRIBUTE_NAME, token);

            updateLastApiCallAttribute(request.getSession());
        }
    }

    private void handleCsrfPingUrl(HttpServletRequest request, HttpServletResponse response, HttpSession httpSession) {
        boolean sessionJustInvalidated = false;

        long deltaMillis = 0L;
        if (httpSession != null) {
            Long lastRealApiRequestMillis = (Long) httpSession.getAttribute(CsrfConstants.NRICH_LAST_REAL_API_REQUEST_MILLIS);
            log.debug("    lastRealApiRequestMillis: {}", lastRealApiRequestMillis);

            if (lastRealApiRequestMillis != null) {
                deltaMillis = System.currentTimeMillis() - lastRealApiRequestMillis;
                log.debug("    deltaMillis: {}", deltaMillis);

                long maxInactiveIntervalMillis = httpSession.getMaxInactiveInterval() * 1000L;
                log.debug("    maxInactiveIntervalMillis: {}", maxInactiveIntervalMillis);

                if ((maxInactiveIntervalMillis > 0) && (deltaMillis > maxInactiveIntervalMillis)) {

                    httpSession.invalidate();

                    sessionJustInvalidated = true;

                    log.debug("    sessionJustInvalidated: {}", true);
                }
            }
        }

        if (!sessionJustInvalidated) {
            csrfTokenManagerService.validateAndRefreshToken(new WebMvcCsrfTokenKeyHolder(request, response, tokenKeyName, CsrfConstants.CSRF_CRYPTO_KEY_NAME));
        }
        else {
            log.debug("    sending csrf stop ping header in response");
            response.setHeader(CsrfConstants.CSRF_PING_STOP_HEADER_NAME, "stopPing");
        }

        response.setHeader(CsrfConstants.CSRF_AFTER_LAST_ACTIVE_REQUEST_MILLIS_HEADER_NAME, Long.toString(deltaMillis));
    }

    private void updateLastApiCallAttribute(HttpSession httpSession) {
        Optional.ofNullable(httpSession).ifPresent(session -> session.setAttribute(CsrfConstants.NRICH_LAST_REAL_API_REQUEST_MILLIS, System.currentTimeMillis()));
    }
}
