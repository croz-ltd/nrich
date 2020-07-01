package net.croz.nrich.security.csrf.webmvc.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.croz.nrich.security.csrf.api.service.CsrfTokenManagerService;
import net.croz.nrich.security.csrf.core.constants.CsrfConstants;
import net.croz.nrich.security.csrf.core.exception.CsrfTokenException;
import net.croz.nrich.security.csrf.core.model.CsrfExcludeConfig;
import net.croz.nrich.security.csrf.core.util.CsrfUriUtil;
import net.croz.nrich.security.csrf.webmvc.holder.WebMvcCsrfTokenHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class CsrfInterceptor extends HandlerInterceptorAdapter {

    private final CsrfTokenManagerService csrfTokenManagerService;

    private final String initialTokenUrl;

    private final String csrfPingUrl;

    private final List<CsrfExcludeConfig> csrfExcludeConfigList;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) {
        log.debug("csrfInterceptor.preHandle()");

        if (handler instanceof ResourceHttpRequestHandler) {
            return true;
        }

        final String pathWithinApplication = new UrlPathHelper().getPathWithinApplication(request);

        if (CsrfConstants.EMPTY_PATH.equals(pathWithinApplication)) {
            return true;
        }

        final HttpSession httpSession = request.getSession(false);
        final String requestUri = request.getRequestURI();

        if (CsrfUriUtil.excludeUri(csrfExcludeConfigList, requestUri)) {

            updateLastApiCallAttribute(httpSession);

        }
        else if (requestUri.endsWith(csrfPingUrl)) {

            handleCsrfPingUrl(request, response, httpSession);

        }
        else if (httpSession != null) {

            csrfTokenManagerService.validateAndRefreshToken(new WebMvcCsrfTokenHolder(request, response));

            updateLastApiCallAttribute(httpSession);
        }
        else {
            // Session doesn't exists, but we should not pass through request.
            throw new CsrfTokenException("Can't validate token. There is no session.");
        }

        return true;
    }

    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final ModelAndView modelAndView) {
        if (request.getRequestURI().endsWith(initialTokenUrl)) {

            modelAndView.addObject(CsrfConstants.CSRF_INITIAL_TOKEN_ATTRIBUTE_NAME, csrfTokenManagerService.generateToken(new WebMvcCsrfTokenHolder(request, response)));

            updateLastApiCallAttribute(request.getSession());
        }
    }

    private void handleCsrfPingUrl(final HttpServletRequest request, final HttpServletResponse response, final HttpSession httpSession) {
        boolean sessionJustInvalidated = false;

        long deltaMillis = 0L;
        if (httpSession != null) {
            final Long lastRealApiRequestMillis = (Long) httpSession.getAttribute(CsrfConstants.NRICH_LAST_REAL_API_REQUEST_MILLIS);
            log.debug("    lastRealApiRequestMillis: {}", lastRealApiRequestMillis);

            if (lastRealApiRequestMillis != null) {
                deltaMillis = System.currentTimeMillis() - lastRealApiRequestMillis;
                log.debug("    deltaMillis: {}", deltaMillis);

                final long maxInactiveIntervalMillis = httpSession.getMaxInactiveInterval() * 1000;
                log.debug("    maxInactiveIntervalMillis: {}", maxInactiveIntervalMillis);

                if ((maxInactiveIntervalMillis > 0) && (deltaMillis > maxInactiveIntervalMillis)) {

                    httpSession.invalidate();

                    sessionJustInvalidated = true;

                    log.debug("    sessionJustInvalidated: {}", true);
                }
            }
        }

        if (!sessionJustInvalidated) {
            csrfTokenManagerService.validateAndRefreshToken(new WebMvcCsrfTokenHolder(request, response));
        }
        else {
            log.debug("    sending csrf stop ping header in response");
            response.setHeader(CsrfConstants.CSRF_PING_STOP_HEADER_NAME, "stopPing");
        }

        response.setHeader(CsrfConstants.CSRF_AFTER_LAST_ACTIVE_REQUEST_MILLIS_HEADER_NAME, Long.valueOf(deltaMillis).toString());
    }

    private void updateLastApiCallAttribute(final HttpSession httpSession) {
        Optional.ofNullable(httpSession).ifPresent(session -> session.setAttribute(CsrfConstants.NRICH_LAST_REAL_API_REQUEST_MILLIS, System.currentTimeMillis()));
    }
}
