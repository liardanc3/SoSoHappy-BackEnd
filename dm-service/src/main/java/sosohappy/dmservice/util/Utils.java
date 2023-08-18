package sosohappy.dmservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class Utils {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    public <T> T jsonToObject(String json, Class<T> targetType) {
        return objectMapper.readValue(json, targetType);
    }

}
