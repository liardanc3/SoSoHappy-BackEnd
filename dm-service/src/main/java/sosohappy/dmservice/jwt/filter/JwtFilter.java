package sosohappy.dmservice.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sosohappy.dmservice.jwt.service.JwtService;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        if(!request.getRequestURI().contains("actuator") && !request.getRequestURI().contains("") && !jwtService.verifyAccessToken(request)){
            response.sendError(403);
            return;
        }

        filterChain.doFilter(request, response);
    }

}
