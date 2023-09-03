package sosohappy.authservice.exception;

import lombok.SneakyThrows;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExceptionAspect {

    @SneakyThrows
    @AfterThrowing(value = "@annotation(convertException)")
    public void convertException(ConvertException convertException) {
        throw convertException.target().getDeclaredConstructor().newInstance();
    }
}
