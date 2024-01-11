package dev.sosohappy.monolithic.service;

import dev.sosohappy.monolithic.exception.custom.ForbiddenException;
import dev.sosohappy.monolithic.exception.custom.UnAuthorizedException;
import dev.sosohappy.monolithic.jwt.service.JwtService;
import dev.sosohappy.monolithic.model.dto.*;
import dev.sosohappy.monolithic.model.entity.User;
import dev.sosohappy.monolithic.oauth2.apple.AppleOAuth2Delegator;
import dev.sosohappy.monolithic.repository.rdbms.FeedLikeNicknameRepository;
import dev.sosohappy.monolithic.repository.rdbms.FeedRepository;
import dev.sosohappy.monolithic.repository.rdbms.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@EnableScheduling
@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class UserService {

    private static final Map<String, String> authorizeCodeAndChallengeMap = new HashMap<>();

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final FeedRepository feedRepository;
    private final FeedLikeNicknameRepository feedLikeNicknameRepository;
    private final AppleOAuth2Delegator appleOAuth2Delegator;

    public void signIn(Map<String, Object> userAttributes, String refreshToken) {
        String email = String.valueOf(userAttributes.get("email"));
        String provider = String.valueOf(userAttributes.get("provider"));
        String providerId = String.valueOf(userAttributes.get("providerId"));
        String deviceToken = String.valueOf(userAttributes.get("deviceToken"));
        String appleRefreshToken = String.valueOf(userAttributes.get("appleRefreshToken"));

        userRepository.findByEmailAndProvider(email, provider)
                        .ifPresentOrElse(
                                user -> {
                                    user.updateRefreshToken(refreshToken);
                                    user.updateDeviceToken(deviceToken);
                                    user.updateProviderId(providerId);
                                    user.updateAppleRefreshToken(appleRefreshToken);
                                },
                                () -> userRepository.save(
                                        User.builder()
                                                .email(email)
                                                .nickname(UUID.randomUUID().toString())
                                                .provider(provider)
                                                .providerId(providerId)
                                                .refreshToken(refreshToken)
                                                .appleRefreshToken(appleRefreshToken)
                                                .deviceToken(deviceToken)
                                                .build()
                                )
                        );
    }

    public ResignDto resign(String email) {
        return userRepository.findByEmail(email)
                .map(user -> {

                    boolean revokeResult = true;

                    if(user.getProvider().equals("apple")){
                        revokeResult = appleOAuth2Delegator.handleAppleUserResign(user.getAppleRefreshToken());
                    }

                    if(revokeResult){
                        userRepository.deleteBlockUserById(user.getId());
                        feedRepository.deleteByNickname(user.getNickname());
                        userRepository.deleteByNickname(user.getNickname());
                    }

                    return ResignDto.builder()
                            .email(email)
                            .success(revokeResult)
                            .build();
                })
                .orElseThrow(UnAuthorizedException::new);
    }

    public DuplicateDto checkDuplicateNickname(String nickname) {
        return userRepository.findByNickname(nickname)
                .map(user ->
                        DuplicateDto.builder()
                                .email(user.getEmail())
                                .isPresent(true)
                                .build()
                )
                .orElseGet(() ->
                        DuplicateDto.builder()
                                .email(null)
                                .isPresent(false)
                                .build()
                );
    }

    public SetProfileDto setProfile(UserRequestDto userRequestDto) {
        return userRepository.findByEmail(userRequestDto.getEmail())
                .map(user -> {
                    feedRepository.updateFeedNickname(user.getNickname(), userRequestDto.getNickname());
                    feedLikeNicknameRepository.updateFeedLikeNickname(user.getNickname(), userRequestDto.getNickname());

                    user.updateProfile(userRequestDto);

                    return SetProfileDto.builder()
                            .email(user.getEmail())
                            .success(true)
                            .build();
                })
                .orElseThrow(UnAuthorizedException::new);
    }

    public UserResponseDto findProfileImg(String nickname) {
        return userRepository.findByNickname(nickname)
                .map(user ->
                        UserResponseDto.builder()
                                .nickname(nickname)
                                .profileImg(user.getProfileImg())
                                .build()
                )
                .orElseGet(() -> UserResponseDto.builder().build());
    }

    public Map<String, String> getAuthorizeCode(String codeChallenge){
        String authorizeCode = UUID.randomUUID().toString();
        authorizeCodeAndChallengeMap.put(authorizeCode, codeChallenge);

        return Map.of("authorizeCode", authorizeCode);
    }

    public Map<String, String> findIntroduction(String nickname) {
        return Map.of(
                "introduction",
                userRepository.findByNickname(nickname)
                        .map(user -> user.getIntroduction() != null ? user.getIntroduction() : "")
                        .orElse("")
        );
    }

    @SneakyThrows
    public NicknameDto signInWithPKCE(SignInDto signInDto, HttpServletResponse response) {

        String provider = signInDto.getProvider();
        String email = signInDto.getEmail() + "+" + provider;
        String providerId = signInDto.getProviderId();
        String authorizeCode = signInDto.getAuthorizeCode();
        String deviceToken = signInDto.getDeviceToken();
        String codeVerifier = signInDto.getCodeVerifier();

        if(!provider.equals("apple") && !provider.equals("google") && !provider.equals("kakao")){
            log.error("unknown provider : " + provider);
            throw new ForbiddenException();
        }

        String encodedCodeChallenge = encode(codeVerifier);

        if(isValidUser(authorizeCode, encodedCodeChallenge)){

            authorizeCodeAndChallengeMap.remove(authorizeCode);

            Map<String, Object> userAttributes = Map.of(
                    "email", email,
                    "provider", provider,
                    "providerId", providerId,
                    "deviceToken", deviceToken,
                    "appleRefreshToken", provider.equals("apple") ?
                            appleOAuth2Delegator.getAppleRefreshToken(signInDto.getAuthorizationCode()) : "-"
            );

            String accessToken = jwtService.createAccessToken(email);
            String refreshToken = jwtService.createRefreshToken(email);

            jwtService.setAccessTokenOnHeader(response, accessToken);
            jwtService.setRefreshTokenOnHeader(response, refreshToken);

            User user = userRepository.findByEmailAndProvider(email, provider).orElse(null);

            response.setCharacterEncoding("UTF-8");
            response.setHeader("email", new String(email.getBytes(), StandardCharsets.UTF_8));

            signIn(userAttributes, refreshToken);

            return new NicknameDto(user != null && user.getNickname().length() <= 10 ? user.getNickname() : "");
        }
        else {
            throw new ForbiddenException();
        }

    }

    public boolean updateBlock(BlockDto blockDto, boolean isBlock) {
        try{
            User srcUser = userRepository.findByNickname(blockDto.getSrcNickname()).orElseThrow();
            User dstUser = userRepository.findByNickname(blockDto.getDstNickname()).orElseThrow();

            if(isBlock){
                if(!srcUser.getBlockUserList().contains(dstUser)){
                    userRepository.insertBlockUser(srcUser.getId(), dstUser.getId());
                }
            } else {
                userRepository.deleteBlockUser(srcUser.getId(), dstUser.getId());
            }

            return true;
        } catch (Exception e){
            return false;
        }
    }

    // ------------------------------------------------------------------------------------------------------------ //


    private String encode(String codeVerifier) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");

        messageDigest.reset();
        messageDigest.update(codeVerifier.getBytes());
        return String.format("%064x", new BigInteger(1, messageDigest.digest()));
    }

    private boolean isValidUser(String authorizeCode, String encodedCodeChallenge) {
        return authorizeCodeAndChallengeMap.containsKey(authorizeCode) && authorizeCodeAndChallengeMap.get(authorizeCode).equals(encodedCodeChallenge);
    }

    @Scheduled(fixedRate = 600000)
    public void deleteAuthorizeCodeAndChallengeMap(){
        authorizeCodeAndChallengeMap.clear();
    }

}
