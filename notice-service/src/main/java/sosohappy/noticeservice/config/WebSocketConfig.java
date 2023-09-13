package sosohappy.noticeservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import sosohappy.noticeservice.handler.NoticeHandler;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class WebSocketConfig implements WebFluxConfigurer {

    private final NoticeHandler handler;

    @Bean
    public HandlerMapping handlerMapping(){
        return new SimpleUrlHandlerMapping(Map.of("/notice-service/connect-notice", handler), 1);
    }


}
