package sosohappy.diaryservice.aspect.exception.custom;

import lombok.Getter;

import java.util.function.Supplier;

@Getter
public class UpdateException extends RuntimeException {
    public UpdateException(String message) {
        super(message);
    }
}
