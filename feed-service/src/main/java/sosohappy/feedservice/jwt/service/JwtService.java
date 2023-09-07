package sosohappy.feedservice.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sosohappy.feedservice.exception.ConvertException;
import sosohappy.feedservice.exception.custom.JWTException;
import sosohappy.feedservice.kafka.KafkaConsumer;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access.header}")
    private String accessHeader;

    private final ConcurrentHashMap<String, String> emailAndTokenMap;

    @ConvertException(target = JWTException.class)
    public boolean verifyAccessToken(HttpServletRequest request) {

        return extractAccessToken(request)
                .filter(token -> isTokenValid(token, extractHeaderEmail(request)))
                .flatMap(this::extractTokenEmail)
                .filter(email -> 
                        emailAndTokenMap.get(email)
                                .equals(extractAccessToken(request).orElse(null))
                )
                .isPresent();
    }
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(accessToken -> accessToken.startsWith("Bearer "))
                .map(accessToken -> accessToken.replace("Bearer ", ""));
    }

    public String extractHeaderEmail(HttpServletRequest request){
        return request.getHeader("Email");
    }

    public Optional<String> extractTokenEmail(String accessToken) {
        try {
            return Optional.ofNullable(
                    JWT.require(Algorithm.HMAC512(secretKey))
                            .build()
                            .verify(accessToken)
                            .getClaim("email")
                            .asString()
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public boolean isTokenValid(String token, String headerEmail) {
        try {
            String tokenEmail = JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(token)
                    .getClaim("email")
                    .asString();
            return headerEmail.equals(tokenEmail);
        } catch (Exception e) {
            return false;
        }
    }
}
