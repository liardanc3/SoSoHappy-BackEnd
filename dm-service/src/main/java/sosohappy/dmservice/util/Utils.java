package sosohappy.dmservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sosohappy.dmservice.aspect.exception.JsonToObjectException;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class Utils {

    private final ObjectMapper objectMapper;

    public <T> T jsonToObject(String json, Class<T> targetType) {
        try {
            return objectMapper.readValue(json, targetType);
        } catch (IOException e) {
            System.out.println("e.getMessage() = " + e.getMessage());
            throw new JsonToObjectException(e.getMessage());
        }
    }

}
