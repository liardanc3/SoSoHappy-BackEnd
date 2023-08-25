package sosohappy.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OauthController {

    @PostMapping("/hello")
    public void hello(){

    }
}
