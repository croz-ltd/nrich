package net.croz.nrich.security.csrf.webflux.stub;

import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import org.springframework.web.server.session.WebSessionManager;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class TestWebSessionManager implements WebSessionManager {

    private final WebSession webSession;

    @Override
    public Mono<WebSession> getSession(ServerWebExchange exchange) {
        return Mono.just(webSession);
    }
}
