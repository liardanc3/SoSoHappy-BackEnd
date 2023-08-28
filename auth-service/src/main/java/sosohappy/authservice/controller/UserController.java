package sosohappy.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import sosohappy.authservice.entity.UserDto;
import sosohappy.authservice.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/checkDuplicateNickname")
    public Boolean checkDuplicateNickname(String nickname){
        return userService.checkDuplicateNickname(nickname);
    }

    @PostMapping("/setProfile")
    public void setProfile(UserDto userDto){
        userService.setProfile(userDto);
    }

}
