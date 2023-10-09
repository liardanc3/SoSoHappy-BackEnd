package sosohappy.noticeservice.jwt.filter;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
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
    public Mono<Void> filter(@NotNull ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        return Mono.defer(() -> {
            if (exchange.getRequest().getURI().getPath().contains("actuator")) {
                return chain.filter(exchange);
            }

            return jwtService.verifyAccessToken(exchange)
                    .flatMap(isValid -> {
                        if (!isValid) {
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            return exchange.getResponse().setComplete();
                        }
                        return chain.filter(exchange);
                    });
        });
    }
}
