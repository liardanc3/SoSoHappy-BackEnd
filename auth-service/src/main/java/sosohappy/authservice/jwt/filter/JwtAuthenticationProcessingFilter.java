package sosohappy.authservice.jwt.filter;

import lombok.SneakyThrows;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import sosohappy.authservice.entity.User;
import sosohappy.authservice.repository.UserRepository;
import sosohappy.authservice.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // login
        if (request.getRequestURI().contains("/oauth2")) {
            filterChain.doFilter(request, response);
            return;
        }

        // accessToken expired
        String refreshToken = jwtService.extractRefreshToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null);
        if (refreshToken != null) {
            reIssueToken(response, refreshToken);
            return;
        }

        checkAccessToken(request, response, filterChain);
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
    public void checkAccessToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
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