package sosohappy.dmservice.exception.custom;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
public class FindMessageException extends RuntimeException{

    public FindMessageException() {}
}