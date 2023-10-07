package sosohappy.noticeservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class TestController {

    @GetMapping("/test-actuator")
    public Mono<String> test(){
        return Mono.just("notice-service on");
    }
}
