package sosohappy.authservice.oauth2.handler;

import lombok.SneakyThrows;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import sosohappy.authservice.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sosohappy.authservice.repository.UserRepository;
import sosohappy.authservice.service.UserService;

import java.util.Map;

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

        String email = String.valueOf(userAttributes.get("email"));

        String accessToken = jwtService.createAccessToken(email);
        String refreshToken = jwtService.createRefreshToken(email);

        jwtService.setAccessTokenOnHeader(response, accessToken);
        jwtService.setRefreshTokenOnHeader(response, refreshToken);

        response.setHeader("nickname", String.valueOf(userRepository.findByEmail(email).orElse(null)));

        userService.signIn(userAttributes, refreshToken);
    }

}