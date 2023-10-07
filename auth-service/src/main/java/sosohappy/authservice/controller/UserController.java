package sosohappy.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import sosohappy.authservice.entity.*;
import sosohappy.authservice.exception.ConvertException;
import sosohappy.authservice.exception.ServerException;
import sosohappy.authservice.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/test-actuator")
    public String test(){
        return "auth-service on";
    }

    @ConvertException(target = ServerException.class)
    @GetMapping("/checkDuplicateNickname")
    public DuplicateDto checkDuplicateNickname(String nickname){
        return userService.checkDuplicateNickname(nickname);
    }

    @ConvertException(target = ServerException.class)
    @PostMapping("/setProfile")
    public SetProfileDto setProfile(UserRequestDto userRequestDto){
        return userService.setProfile(userRequestDto);
    }

    @ConvertException(target = ServerException.class)
    @PostMapping("/resign")
    public ResignDto resign(String email){
        return userService.resign(email);
    }

    @ConvertException(target = ServerException.class)
    @PostMapping(value = "/findProfileImg")
    public UserResponseDto findProfileImg(String nickname) {
        return userService.findProfileImg(nickname);
    }
}
