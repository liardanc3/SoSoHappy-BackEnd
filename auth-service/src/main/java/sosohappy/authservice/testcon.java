package sosohappy.authservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testcon {

    @GetMapping("/hello")
    public String he(){
        return "hello";
    }
}
