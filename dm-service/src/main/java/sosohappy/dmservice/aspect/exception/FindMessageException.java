package sosohappy.dmservice.aspect.exception;

import lombok.Getter;

@Getter
public class FindMessageException extends RuntimeException{

    public FindMessageException(String message) { super(message); }
}