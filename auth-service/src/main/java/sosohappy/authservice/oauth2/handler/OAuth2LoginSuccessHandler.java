package sosohappy.authservice.oauth2.handler;

import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import sosohappy.authservice.entity.User;
import sosohappy.authservice.repository.UserRepository;
import sosohappy.authservice.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");

        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

        String accessToken = jwtService.createAccessToken(oAuth2User.getName());

        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);

        jwtService.sendAccessAndRefreshToken(response, accessToken, null);
        loginSuccess(response, oAuth2User);
    }

    // TODO : 소셜 로그인 시에도 무조건 토큰 생성하지 말고 JWT 인증 필터처럼 RefreshToken 유/무에 따라 다르게 처리해보기
    private void loginSuccess(HttpServletResponse response, DefaultOAuth2User oAuth2User) throws IOException {
        System.out.println("OAuth2LoginSuccessHandler.loginSuccess");
        String accessToken = jwtService.createAccessToken(oAuth2User.getName());
        String refreshToken = jwtService.createRefreshToken();
        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
        response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtService.updateRefreshToken(oAuth2User.getName(), refreshToken);
    }
}