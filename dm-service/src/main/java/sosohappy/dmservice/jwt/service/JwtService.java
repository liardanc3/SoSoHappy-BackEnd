package sosohappy.dmservice.jwt.service;

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
                .filter(token -> isTokenValid(token, extractHeaderEmail(exchange.getRequest()).orElse(null)))
                .isPresent();
    }

    public Optional<String> extractAccessToken(ServerHttpRequest request) {
        return Optional.ofNullable(request.getHeaders().getFirst("Authorization"))
                .filter(accessToken -> accessToken.startsWith("Bearer "))
                .map(accessToken -> accessToken.replace("Bearer ", ""));
    }

    public Optional<String> extractHeaderEmail(ServerHttpRequest request) {
        return Optional.ofNullable(request.getHeaders().getFirst("Email"))
                .filter(accessToken -> accessToken.startsWith("Bearer "))
                .map(accessToken -> accessToken.replace("Bearer ", ""));
    }

    public boolean isTokenValid(String email, String token){
        if(email == null || token == null || emailAndTokenMap.get(email) == null || !emailAndTokenMap.get(email).equals(token)){
            return false;
        }
        return true;
    }

}
