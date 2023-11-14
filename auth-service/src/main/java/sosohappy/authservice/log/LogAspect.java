package sosohappy.authservice.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LogAspect {

    @Before("execution(* sosohappy.authservice.controller.*.*(..))")
    public void handleBefore(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        StringBuilder sb = new StringBuilder();

        String email = request.getHeader("Email");
        String servletPath = request.getServletPath();
        String sessionId = request.getSession().getId();

        sb.append(String.format("REQUEST %s : %s\n\n", servletPath, email != null ? email : sessionId));

        Arrays.stream(joinPoint.getArgs())
                .filter(obj -> obj.toString().length() <= 5000)
                .forEach(sb::append);

        request.getHeaderNames().asIterator().forEachRemaining(
                headerName -> sb.append(headerName).append(" : ").append(request.getHeader(headerName)).append("\n")
        );

        log.info(sb.toString());
    }

    @AfterReturning(value = "execution(* sosohappy.authservice.controller.*.*(..))", returning = "result")
    public void handleAfterReturning(Object result) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();

        StringBuilder sb = new StringBuilder();

        String email = request.getHeader("Email");
        String servletPath = request.getServletPath();
        String sessionId = request.getSession().getId();

        sb.append(String.format("RESPONSE %d %s : %s\n\n", response.getStatus(), servletPath, email != null ? email : sessionId));

        sb.append(result);
        sb.append("\n");

        for (String headerName : response.getHeaderNames()) {
            String str = response.getHeader(headerName);

            sb.append(headerName).append(" : ").append(str).append("\n");

        }

        log.info(sb.toString());
    }

    @AfterThrowing(value = "execution(* sosohappy.authservice.controller.*.*(..))", throwing = "ex")
    public void handleAfterThrowing(Exception ex){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        StringBuilder sb = new StringBuilder();

        String email = request.getHeader("Email");
        String servletPath = request.getServletPath();
        String sessionId = request.getSession().getId();

        sb.append(String.format("RESPONSE ERROR %s : %s\n\n", servletPath, email != null ? email : sessionId));
        sb.append(Arrays.toString(ex.getStackTrace()));

        log.info(sb.toString());
    }
}
