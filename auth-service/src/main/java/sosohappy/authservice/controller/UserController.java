package sosohappy.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import sosohappy.authservice.entity.DuplicateDto;
import sosohappy.authservice.entity.ResignDto;
import sosohappy.authservice.entity.SetProfileDto;
import sosohappy.authservice.entity.UserDto;
import sosohappy.authservice.exception.ConvertException;
import sosohappy.authservice.exception.ServerException;
import sosohappy.authservice.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ConvertException(target = ServerException.class)
    @GetMapping("/checkDuplicateNickname")
    public DuplicateDto checkDuplicateNickname(String nickname){
        return userService.checkDuplicateNickname(nickname);
    }

    @ConvertException(target = ServerException.class)
    @PostMapping("/setProfile")
    public SetProfileDto setProfile(UserDto userDto){
        return userService.setProfile(userDto);
    }

    @ConvertException(target = ServerException.class)
    @PostMapping("/resign")
    public ResignDto resign(String email){
        return userService.resign(email);
    }

}
