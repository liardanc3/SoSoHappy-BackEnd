package sosohappy.authservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sosohappy.authservice.entity.*;
import sosohappy.authservice.service.UserService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/test-actuator")
    public String test(){
        return "auth-service on";
    }

    @GetMapping("/checkDuplicateNickname")
    public DuplicateDto checkDuplicateNickname(String nickname){
        return userService.checkDuplicateNickname(nickname);
    }

    @PostMapping("/setProfile")
    public SetProfileDto setProfile(UserRequestDto userRequestDto){
        return userService.setProfile(userRequestDto);
    }

    @PostMapping("/resign")
    public ResignDto resign(String email){
        return userService.resign(email);
    }

    @PostMapping(value = "/findProfileImg")
    public UserResponseDto findProfileImg(String nickname) {
        return userService.findProfileImg(nickname);
    }

    @PostMapping("/signIn")
    public void signInWithPKCE(@ModelAttribute SignInDto signInDto, HttpServletResponse httpServletResponse) {
        userService.signInWithPKCE(signInDto, httpServletResponse);
    }

    @PostMapping("/getAuthorizeCode")
    public Map<String, String> getAuthorizeCode(@NotNull String codeChallenge){
        return userService.getAuthorizeCode(codeChallenge);
    }
}
