package sosohappy.dmservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import sosohappy.dmservice.handler.MessageHandler;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class WebSocketConfig implements WebFluxConfigurer {

    private final MessageHandler handler;

    @Bean
    public HandlerMapping handlerMapping(){
        return new SimpleUrlHandlerMapping(Map.of("/dm-service", handler), 1);
    }
}
