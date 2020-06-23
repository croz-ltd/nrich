package net.croz.nrich.security.csrf.webmvc.holder;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.security.csrf.api.holder.CsrfTokenHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;

@RequiredArgsConstructor
public class WebMvcCsrfTokenHolder implements CsrfTokenHolder {

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    @Override
    public String getToken(final String tokenKeyName) {
        String token = request.getHeader(tokenKeyName);

        if (token == null) {
            token = request.getParameter(tokenKeyName);
        }

        return token;
    }

    @Override
    public void storeToken(final String tokenKeyName, final String csrfToken) {
        response.setHeader(tokenKeyName, csrfToken);
    }

    @Override
    public Key getCryptoKey(final String cryptoKeyName) {
        return (Key) request.getSession().getAttribute(cryptoKeyName);
    }

    @Override
    public void storeCryptoKey(final String cryptoKeyName, final Key cryptoKey) {
        request.getSession().setAttribute(cryptoKeyName, cryptoKey);
    }
}
