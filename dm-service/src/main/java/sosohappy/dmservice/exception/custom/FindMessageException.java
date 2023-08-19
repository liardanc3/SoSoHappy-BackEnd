package sosohappy.dmservice.exception.custom;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FindMessageException extends RuntimeException{

    public FindMessageException() {}
}