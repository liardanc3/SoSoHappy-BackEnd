package sosohappy.dmservice.aspect.exception;

import lombok.Getter;

@Getter
public class MessageToJsonException extends RuntimeException{

    public MessageToJsonException(String message) {super(message);}
}