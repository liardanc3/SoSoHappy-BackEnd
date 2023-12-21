package dev.sosohappy.monolithic.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class Utils {

    public static final ConcurrentHashMap<String, String> emailAndDeviceTokenMap = null;

    static {
        final ConcurrentHashMap<String, String> emailAndDeviceTokenMap = new ConcurrentHashMap<>();
    }

    private final ObjectMapper objectMapper;

    @SneakyThrows
    public <T> T jsonToObject(String json, Class<T> targetType) {
        return objectMapper.readValue(json, targetType);
    }

}
