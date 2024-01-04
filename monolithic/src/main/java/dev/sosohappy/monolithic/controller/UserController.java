package dev.sosohappy.monolithic.controller;

import dev.sosohappy.monolithic.model.dto.*;
import dev.sosohappy.monolithic.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/auth-service")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/checkDuplicateNickname")
    public DuplicateDto checkDuplicateNickname(@Valid NicknameDto nicknameDto){
        return userService.checkDuplicateNickname(nicknameDto.getNickname());
    }

    @PostMapping("/setProfile")
    public SetProfileDto setProfile(@Valid UserRequestDto userRequestDto){
        return userService.setProfile(userRequestDto);
    }

    @PostMapping("/resign")
    public ResignDto resign(@Valid EmailDto emailDto){
        return userService.resign(emailDto.getEmail());
    }

    @PostMapping("/findProfileImg")
    public UserResponseDto findProfileImg(@Valid NicknameDto nicknameDto) {
        return userService.findProfileImg(nicknameDto.getNickname());
    }

    @PostMapping(value = "/findIntroduction")
    public Map<String, String> findIntroduction(@Valid NicknameDto nicknameDto) {
        return userService.findIntroduction(nicknameDto.getNickname());
    }

    @PostMapping("/signIn")
    public NicknameDto signInWithPKCE(@ModelAttribute @Valid SignInDto signInDto, HttpServletResponse httpServletResponse) {
        return userService.signInWithPKCE(signInDto, httpServletResponse);
    }

    @PostMapping("/getAuthorizeCode")
    public Map<String, String> getAuthorizeCode(@Valid CodeChallengeDto codeChallengeDto){
        return userService.getAuthorizeCode(codeChallengeDto.getCodeChallenge());
    }

    @PostMapping("/block")
    public Map<String, Boolean> block(@Valid BlockDto blockDto){
        return Map.of("success", userService.updateBlock(blockDto, true));
    }

    @PostMapping("/unblock")
    public Map<String, Boolean> unblock(@Valid BlockDto blockDto){
        return Map.of("success", userService.updateBlock(blockDto, false));
    }
}
