package sosohappy.authservice.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.hc.core5.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import sosohappy.authservice.entity.User;
import sosohappy.authservice.jwt.service.JwtService;
import sosohappy.authservice.repository.UserRepository;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 로그인
        if (request.getRequestURI().contains("/oauth2")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 재발급
        if (request.getRequestURI().contains("/reIssueToken")){
            String attributeEmail = request.getHeader("Email");
            String tokenEmail = jwtService.extractEmail(
                    jwtService.extractAccessToken(request).orElse(null)
            ).orElse(null);

            String refreshToken = jwtService.extractRefreshToken(request)
                    .filter(jwtService::isTokenValid)
                    .orElse(null);

            if (tokenEmail != null && attributeEmail.equals(tokenEmail) && refreshToken != null) {
                reIssueToken(response, refreshToken);
                response.sendError(HttpStatus.SC_OK);
                return;
            }

            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // verify
        verifyAccessToken(request, response, filterChain);
    }

    public void reIssueToken(HttpServletResponse response, String refreshToken) {
        userRepository.findByRefreshToken(refreshToken)
                .ifPresentOrElse(user -> {
                            jwtService.setAccessTokenOnHeader(response, jwtService.createAccessToken(user.getEmail()));
                            jwtService.setRefreshTokenOnHeader(response, reIssuedRefreshToken(user));
                        },
                        () -> {
                            try {
                                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
    }

    public String reIssuedRefreshToken(User user) {
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        user.updateRefreshToken(reIssuedRefreshToken);
        userRepository.save(user);
        return reIssuedRefreshToken;
    }

    @SneakyThrows
    public void verifyAccessToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {

        Optional<User> user = jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid)
                .flatMap(jwtService::extractEmail)
                .flatMap(userRepository::findByEmail);

        if(user.isEmpty()){
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        filterChain.doFilter(request, response);
    }

}