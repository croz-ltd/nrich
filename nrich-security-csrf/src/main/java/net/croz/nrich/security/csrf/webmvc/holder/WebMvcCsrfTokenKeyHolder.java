package net.croz.nrich.security.csrf.webmvc.holder;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.security.csrf.api.holder.CsrfTokenKeyHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;

@RequiredArgsConstructor
public class WebMvcCsrfTokenKeyHolder implements CsrfTokenKeyHolder {

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    private final String tokenKeyName;

    private final String cryptoKeyName;

    @Override
    public String getToken() {
        String token = request.getHeader(tokenKeyName);

        if (token == null) {
            token = request.getParameter(tokenKeyName);
        }

        return token;
    }

    @Override
    public void storeToken(final String csrfToken) {
        response.setHeader(tokenKeyName, csrfToken);
    }

    @Override
    public Key getCryptoKey() {
        return (Key) request.getSession().getAttribute(cryptoKeyName);
    }

    @Override
    public void storeCryptoKey(final Key cryptoKey) {
        request.getSession().setAttribute(cryptoKeyName, cryptoKey);
    }
}
