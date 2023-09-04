package sosohappy.feedservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sosohappy.feedservice.domain.dto.UpdateResultDto;
import sosohappy.feedservice.exception.custom.UpdateException;

@RestControllerAdvice
public class ResponseExceptionHandler {

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(UpdateException.class)
    public UpdateResultDto handleUpdateException(){
        return UpdateResultDto.updateFailure();
    }


}


