package sosohappy.authservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
public class ServerException extends RuntimeException{

    public ServerException(String message) {
        super(message);
    }
}