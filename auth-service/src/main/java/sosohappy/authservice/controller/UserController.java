package sosohappy.authservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;
import sosohappy.authservice.entity.*;
import sosohappy.authservice.service.UserService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/checkDuplicateNickname")
    public DuplicateDto checkDuplicateNickname(@Valid @NotEmpty String nickname){
        return userService.checkDuplicateNickname(nickname);
    }

    @PostMapping("/setProfile")
    public SetProfileDto setProfile(@Valid UserRequestDto userRequestDto){
        return userService.setProfile(userRequestDto);
    }

    @PostMapping("/resign")
    public ResignDto resign(@Valid @NotEmpty String email){
        return userService.resign(email);
    }

    @PostMapping("/findProfileImg")
    public UserResponseDto findProfileImg(@Valid @NotEmpty String nickname) {
        return userService.findProfileImg(nickname);
    }

    @PostMapping(value = "/findIntroduction")
    public Map<String, String> findIntroduction(@Valid @NotEmpty String nickname) {
        return userService.findIntroduction(nickname);
    }

    @PostMapping("/signIn")
    public void signInWithPKCE(@ModelAttribute @Valid SignInDto signInDto, HttpServletResponse httpServletResponse) {
        userService.signInWithPKCE(signInDto, httpServletResponse);
    }

    @PostMapping("/getAuthorizeCode")
    public Map<String, String> getAuthorizeCode(@Valid @NotNull String codeChallenge){
        return userService.getAuthorizeCode(codeChallenge);
    }
}
