package sosohappy.feedservice.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import sosohappy.feedservice.exception.ConvertException;
import sosohappy.feedservice.exception.custom.JWTException;
import sosohappy.feedservice.kafka.KafkaConsumer;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final ConcurrentHashMap<String, String> emailAndTokenMap;

    @ConvertException(target = JWTException.class)
    public boolean verifyAccessToken(HttpServletRequest request) {
        return extractAccessToken(request)
                .filter(token -> isTokenValid(extractHeaderEmail(request).orElse(null), token))
                .isPresent();
    }

    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(accessToken -> accessToken.startsWith("Bearer "))
                .map(accessToken -> accessToken.replace("Bearer ", ""));
    }

    public Optional<String> extractHeaderEmail(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Email"));
    }

    public boolean isTokenValid(String email, String token){
        return email != null && token != null && emailAndTokenMap.get(email) != null && emailAndTokenMap.get(email).equals(token);
    }

}
