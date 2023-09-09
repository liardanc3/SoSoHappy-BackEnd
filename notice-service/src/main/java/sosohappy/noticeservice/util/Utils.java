package sosohappy.noticeservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class Utils {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    public String objectToString(Object json) {
        return objectMapper.writeValueAsString(json);
    }

}
