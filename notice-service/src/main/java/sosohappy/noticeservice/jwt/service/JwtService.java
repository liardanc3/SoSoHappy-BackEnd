package sosohappy.noticeservice.jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final ConcurrentHashMap<String, String> emailAndTokenMap;

    public Mono<Boolean> verifyAccessToken(ServerWebExchange exchange) {
        return extractAccessToken(exchange.getRequest())
                .flatMap(token -> {
                    String email = extractHeaderEmail(exchange.getRequest());
                    return Mono.just(isTokenValid(email, token));
                });
    }

    public Mono<String> extractAccessToken(ServerHttpRequest request) {
        return Mono.justOrEmpty(request.getHeaders().getFirst("Authorization"))
                .filter(accessToken -> accessToken.startsWith("Bearer "))
                .map(accessToken -> accessToken.replace("Bearer ", ""));
    }

    public String extractHeaderEmail(ServerHttpRequest request) {
        return request.getHeaders().getFirst("Email");
    }

    public boolean isTokenValid(String email, String token){
        return email != null && token != null && emailAndTokenMap.get(email) != null && emailAndTokenMap.get(email).equals(token);
    }

}
