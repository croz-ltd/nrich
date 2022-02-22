package net.croz.nrich.security.csrf.webflux.holder;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.security.csrf.api.holder.CsrfTokenKeyHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;

import java.security.Key;

@RequiredArgsConstructor
public class WebFluxCsrfTokenKeyHolder implements CsrfTokenKeyHolder {

    private final ServerWebExchange exchange;

    private final WebSession webSession;

    private final String tokenKeyName;

    private final String cryptoKeyName;

    @Override
    public String getToken() {
        String token = exchange.getRequest().getHeaders().getFirst(tokenKeyName);

        if (token == null) {
            token = exchange.getRequest().getQueryParams().getFirst(tokenKeyName);
        }

        return token;
    }

    @Override
    public void storeToken(String csrfToken) {
        exchange.getResponse().getHeaders().add(tokenKeyName, csrfToken);
    }

    @Override
    public Key getCryptoKey() {
        return (Key) webSession.getAttributes().get(cryptoKeyName);
    }

    @Override
    public void storeCryptoKey(Key cryptoKey) {
        webSession.getAttributes().put(cryptoKeyName, cryptoKey);
    }
}
