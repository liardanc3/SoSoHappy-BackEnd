package sosohappy.noticeservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class UtilConfig {

    @Bean
    public ConcurrentHashMap<String, String> stringAndStringMap(){
        return new ConcurrentHashMap<>();
    }

}
