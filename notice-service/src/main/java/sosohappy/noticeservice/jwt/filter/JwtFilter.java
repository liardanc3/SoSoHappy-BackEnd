package sosohappy.noticeservice.jwt.filter;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import sosohappy.noticeservice.jwt.service.JwtService;

@Component
@RequiredArgsConstructor
public class JwtFilter implements WebFilter {

    private final JwtService jwtService;

    @NotNull
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if(!jwtService.verifyAccessToken(exchange)) {
            exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(403));
            return Mono.empty();
        }

        return chain.filter(exchange);
    }
}
