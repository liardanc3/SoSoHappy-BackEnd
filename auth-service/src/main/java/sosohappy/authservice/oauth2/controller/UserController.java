package sosohappy.authservice.oauth2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import sosohappy.authservice.entity.ResignDto;
import sosohappy.authservice.oauth2.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/resign")
    public ResignDto resign(String email){
        return userService.resign(email);
    }

}
