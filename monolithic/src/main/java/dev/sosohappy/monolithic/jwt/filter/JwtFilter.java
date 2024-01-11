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
import java.util.Enumeration;
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
    private static String favico = "/favicon.ico";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        boolean isSignIn = request.getRequestURI().startsWith(signIn);
        boolean isReIssueToken = request.getRequestURI().startsWith(reIssueToken);
        boolean isGetAuthorizeCode = request.getRequestURI().startsWith(getAuthorizeCode);
        boolean isImage = request.getRequestURI().startsWith(image);
        boolean isFavicon = request.getRequestURI().startsWith(favico);
        boolean isIndex = request.getRequestURI().equals("/");
        
        String uri = request.getRequestURI();
        String email = request.getHeader("email");
        
        if(email == null){
            email = request.getSession().getId();
        }

        if(isIndex){
            response.getWriter().println("sosohappy don't operate a website.");
            return;
        }

        if (isSignIn || isGetAuthorizeCode || isImage || isFavicon || true) {
            filterChain.doFilter(request, response);
            generateLog(email, uri, response);
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
                generateLog(email, uri, response);
                return;
            }

            response.setStatus(403);
            generateLog(email, uri, response);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        verifyAccessToken(email, uri, request, response, filterChain);
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
    public void verifyAccessToken(String email, String uri, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        Optional<User> user = jwtService.extractAccessToken(request)
                .filter(token -> jwtService.isTokenValid(token, jwtService.extractHeaderEmail(request)))
                .flatMap(jwtService::extractTokenEmail)
                .flatMap(userRepository::findByEmail);

        if(user.isEmpty()){
            response.setStatus(403);
            generateLog(email, uri, response);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        filterChain.doFilter(request, response);
        generateLog(email, uri, response);
    }

    private void generateLog(String email, String uri, HttpServletResponse response){
        if(response.getStatus() >= 400){
            log.error(
                    String.format(
                            "RESPONSE %d [%32.32s] from %s",
                            response.getStatus(), uri, email
                    )
            );
        } else {
            log.info(
                    String.format(
                            "RESPONSE %d [%32.32s] from %s",
                            response.getStatus(), uri, email
                    )
            );
        }

    }
}
