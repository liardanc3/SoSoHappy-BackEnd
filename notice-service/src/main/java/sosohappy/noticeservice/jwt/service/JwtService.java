package sosohappy.noticeservice.jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final ConcurrentHashMap<String, String> emailAndTokenMap;

    public boolean verifyAccessToken(ServerWebExchange exchange) {
        return extractAccessToken(exchange.getRequest())
                .filter(token -> isTokenValid(extractHeaderEmail(exchange.getRequest()).orElse(null), token))
                .isPresent();
    }

    public Optional<String> extractAccessToken(ServerHttpRequest request) {
        return Optional.ofNullable(request.getHeaders().getFirst("Authorization"))
                .filter(accessToken -> accessToken.startsWith("Bearer "))
                .map(accessToken -> accessToken.replace("Bearer ", ""));
    }

    public Optional<String> extractHeaderEmail(ServerHttpRequest request) {
        return Optional.ofNullable(request.getHeaders().getFirst("Email"));
    }

    public boolean isTokenValid(String email, String token){
        return email != null && token != null && emailAndTokenMap.get(email) != null && emailAndTokenMap.get(email).equals(token);
    }

}
