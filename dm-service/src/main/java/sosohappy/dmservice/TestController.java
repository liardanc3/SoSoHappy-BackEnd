package sosohappy.dmservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class TestController {

    @GetMapping("/test")
    Flux<String> configTest(@Value("${test.now}") String value){
        return Flux.just("test.now = ", value);
    }
}
