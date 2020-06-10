package net.croz.nrich.security.csrf.webflux.holder;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.security.csrf.core.holder.CsrfTokenHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;

import java.security.Key;

@RequiredArgsConstructor
public class WebFluxCsrfTokenHolder implements CsrfTokenHolder {

    private final ServerWebExchange exchange;

    private final WebSession webSession;

    @Override
    public String getToken(final String tokenKeyName) {
        String token = exchange.getRequest().getHeaders().getFirst(tokenKeyName);

        if (token == null) {
            token = exchange.getRequest().getQueryParams().getFirst(tokenKeyName);
        }

        return token;
    }

    @Override
    public void storeToken(final String tokenKeyName, final String csrfToken) {
        exchange.getResponse().getHeaders().add(tokenKeyName, csrfToken);
    }

    @Override
    public Key getCryptoKey(String cryptoKeyName) {
        return (Key) webSession.getAttributes().get(cryptoKeyName);
    }

    @Override
    public void storeCryptoKey(final String cryptoKeyName, final Key cryptoKey) {
        webSession.getAttributes().put(cryptoKeyName, cryptoKey);
    }
}
