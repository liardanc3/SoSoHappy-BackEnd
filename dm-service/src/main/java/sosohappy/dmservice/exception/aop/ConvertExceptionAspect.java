package sosohappy.dmservice.exception.aop;

import lombok.SneakyThrows;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import sosohappy.dmservice.exception.annotation.ConvertException;

@Aspect
@Component
public class ConvertExceptionAspect {

    @SneakyThrows
    @AfterThrowing(value = "@annotation(convertException)")
    public void convertException(ConvertException convertException) {
        throw convertException.target().getDeclaredConstructor().newInstance();
    }
}
