package sosohappy.feedservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sosohappy.feedservice.exception.custom.UpdateException;
import sosohappy.feedservice.domain.dto.UpdateResultDto;

@RestControllerAdvice
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ResponseExceptionHandler {

    @ExceptionHandler(UpdateException.class)
    public UpdateResultDto handleUpdateException(UpdateException e){
        return UpdateResultDto.builder()
                .success(false)
                .message(e.getMessage())
                .build();
    }
}


