package sosohappy.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OauthController {

    @GetMapping("/kakao")
    public void getKakaoAccessCode(@RequestParam("code") String code){
        System.out.println("code = " + code);
    }
}
