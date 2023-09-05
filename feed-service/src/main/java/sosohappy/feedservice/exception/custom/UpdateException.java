package sosohappy.feedservice.exception.custom;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.function.Supplier;

@Getter
public class UpdateException extends RuntimeException {
    public UpdateException() {
    }

}
