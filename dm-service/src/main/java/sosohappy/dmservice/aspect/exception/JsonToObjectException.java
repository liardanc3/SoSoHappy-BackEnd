package sosohappy.dmservice.aspect.exception;

import lombok.Getter;

@Getter
public class JsonToObjectException extends RuntimeException{

    public JsonToObjectException(String message) {super(message);}
}