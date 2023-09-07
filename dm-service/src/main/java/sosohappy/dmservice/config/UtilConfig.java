package sosohappy.dmservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class UtilConfig {

    @Bean
    ConcurrentHashMap<String, String> StringAndStringConcurrentHashMap(){
        return new ConcurrentHashMap<>();
    }

    @Bean
    HashMap<String, String> StringAndStringMap() { return new HashMap<>(); }

    @Bean
    HashMap<String, WebSocketSession> StringAndWebSocketSessionMap() { return new HashMap<>(); }
}
