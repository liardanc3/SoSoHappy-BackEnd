package sosohappy.feedservice.jwt.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sosohappy.feedservice.kafka.KafkaConsumer;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtService {

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
        return email != null && token != null && KafkaConsumer.emailAndTokenMap.get(email) != null && KafkaConsumer.emailAndTokenMap.get(email).equals(token);
    }

}
