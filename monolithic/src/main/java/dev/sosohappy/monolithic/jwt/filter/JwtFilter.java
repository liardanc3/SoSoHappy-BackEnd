package dev.sosohappy.monolithic.jwt.filter;

import dev.sosohappy.monolithic.jwt.service.JwtService;
import dev.sosohappy.monolithic.model.entity.User;
import dev.sosohappy.monolithic.repository.rdbms.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    private static String signIn = "/auth-service/signIn";
    private static String getAuthorizeCode = "/auth-service/getAuthorizeCode";
    private static String reIssueToken = "/auth-service/reIssueToken";
    private static String image = "/feed-service/image";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        boolean isSignIn = request.getRequestURI().startsWith(signIn);
        boolean isReIssueToken = request.getRequestURI().startsWith(reIssueToken);
        boolean isGetAuthorizeCode = request.getRequestURI().startsWith(getAuthorizeCode);
        boolean isImage = request.getRequestURI().startsWith(image);

        if (isSignIn || isGetAuthorizeCode || isImage) {
            filterChain.doFilter(request, response);

            generateLog(request, response);
            return;
        }

        if (isReIssueToken){
            String headerEmail = jwtService.extractHeaderEmail(request);

            String tokenEmail = jwtService.extractTokenEmail(jwtService.extractRefreshToken(request).orElse(null))
                    .orElse(null);

            String refreshToken = jwtService.extractRefreshToken(request)
                    .filter(token -> jwtService.isTokenValid(token, headerEmail))
                    .orElse(null);

            if (headerEmail.equals(tokenEmail) && refreshToken != null) {
                reIssueToken(response, refreshToken);
                generateLog(request, response);
                return;
            }
            
            generateLog(request, response);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

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
        String reIssuedRefreshToken = jwtService.createRefreshToken(user.getEmail());
        user.updateRefreshToken(reIssuedRefreshToken);
        userRepository.save(user);
        return reIssuedRefreshToken;
    }

    @SneakyThrows
    public void verifyAccessToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        Optional<User> user = jwtService.extractAccessToken(request)
                .filter(token -> jwtService.isTokenValid(token, jwtService.extractHeaderEmail(request)))
                .flatMap(jwtService::extractTokenEmail)
                .flatMap(userRepository::findByEmail);

        if(user.isEmpty()){
            generateLog(request, response);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        filterChain.doFilter(request, response);
        generateLog(request, response);
    }

    private void generateLog(HttpServletRequest request, HttpServletResponse response){
        String email = request.getHeader("email");
        log.info("[RESPONSE " + response.getStatus() + " " + request.getRequestURI()  + "] : " + (email != null ? email : request.getSession().getId()));
    }
}
