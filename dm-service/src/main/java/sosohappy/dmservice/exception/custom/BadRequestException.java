package sosohappy.dmservice.exception.custom;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException{

    public BadRequestException() {}
}