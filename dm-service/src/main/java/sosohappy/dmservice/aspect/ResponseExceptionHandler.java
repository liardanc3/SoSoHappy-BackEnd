package sosohappy.dmservice.aspect;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;
import sosohappy.dmservice.aspect.exception.FindMessageException;
import sosohappy.dmservice.domain.dto.ExceptionDto;

@RestControllerAdvice
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ResponseExceptionHandler {

    @ExceptionHandler(FindMessageException.class)
    public Mono<ExceptionDto> handleFindMessageException(){
        return Mono.just(
                ExceptionDto.builder()
                        .errorMessage("Error occur : findMessage")
                        .build()
        );
    }

}

