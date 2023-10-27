package sosohappy.authservice.oauth2.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import sosohappy.authservice.entity.User;
import sosohappy.authservice.jwt.service.JwtService;
import sosohappy.authservice.repository.UserRepository;
import sosohappy.authservice.service.UserService;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    @SneakyThrows
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Map<String, Object> userAttributes = ((DefaultOAuth2User) authentication.getPrincipal()).getAttributes();

        String provider = String.valueOf(userAttributes.get("provider"));
        String email = userAttributes.get("email") + "+" + provider;

        String accessToken = jwtService.createAccessToken(email);
        String refreshToken = jwtService.createRefreshToken(email);

        jwtService.setAccessTokenOnHeader(response, accessToken);
        jwtService.setRefreshTokenOnHeader(response, refreshToken);

        User user = userRepository.findByEmailAndProvider(email,provider).orElse(null);

        response.setHeader("nickname", user != null && user.getNickname() != null > 10 ? Objects.requireNonNull(user).getNickname() : null);
        response.setHeader("email", email);

        userService.signIn(userAttributes, refreshToken);
    }

}
