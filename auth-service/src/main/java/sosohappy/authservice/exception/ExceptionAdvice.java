package sosohappy.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sosohappy.authservice.entity.ExceptionDto;

@RestControllerAdvice
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ExceptionAdvice {

    @ExceptionHandler(ServerException.class)
    public ExceptionDto handleUpdateException(ServerException e){
        return new ExceptionDto(e.getMessage());
    }
}
