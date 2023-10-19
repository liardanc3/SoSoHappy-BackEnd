package sosohappy.authservice.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sosohappy.authservice.entity.*;
import sosohappy.authservice.exception.custom.BadRequestException;
import sosohappy.authservice.exception.custom.ForbiddenException;
import sosohappy.authservice.jwt.service.JwtService;
import sosohappy.authservice.kafka.KafkaProducer;
import sosohappy.authservice.repository.UserRepository;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EnableScheduling
@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

    private static Map<String, String> authorizeCodeAndChallengeMap = new HashMap<>();

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectProvider<UserService> userServiceProvider;

    public void signIn(Map<String, Object> userAttributes, String refreshToken) {
        String email = String.valueOf(userAttributes.get("email"));
        String provider = String.valueOf(userAttributes.get("provider"));
        String providerId = String.valueOf(userAttributes.get("providerId"));

        userRepository.findByEmailAndProvider(email, provider)
                        .ifPresentOrElse(
                                user -> user.updateRefreshToken(refreshToken),
                                () -> userRepository.save(
                                        User.builder()
                                                .email(email)
                                                .provider(provider)
                                                .providerId(providerId)
                                                .refreshToken(refreshToken)
                                                .build()
                                )
                        );
    }

    public ResignDto resign(String email) {
        return userRepository.findByEmail(email)
                .map(user -> {

                    userServiceProvider.getObject()
                            .produceResign(user.getEmail(), user.getNickname());

                    userRepository
                            .delete(user);

                    return ResignDto.builder()
                            .email(email)
                            .success(true)
                            .build();
                })
                .orElseGet(() ->
                        ResignDto.builder()
                                .email(email)
                                .success(false)
                                .build()
                );
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
                    user.updateProfile(userRequestDto);
                    return SetProfileDto.builder()
                            .email(user.getEmail())
                            .success(true)
                            .build();
                })
                .orElseGet(() ->
                        SetProfileDto.builder()
                                .email(userRequestDto.getEmail())
                                .success(false)
                                .build()
                );
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
        if(codeChallenge == null){
            throw new BadRequestException();
        }

        String authorizeCode = UUID.randomUUID().toString();
        authorizeCodeAndChallengeMap.put(authorizeCode, codeChallenge);

        return Map.of("authorizeCode", authorizeCode);
    }

    @SneakyThrows
    public void signInWithPKCE(SignInDto signInDto, HttpServletResponse response) {

        String email = signInDto.getEmail();
        String provider = signInDto.getProvider();

        if(!provider.equals("apple") && !provider.equals("google") && !provider.equals("kakao")){
            throw new ForbiddenException();
        }

        String providerId = signInDto.getProviderId();

        String authorizeCode = signInDto.getAuthorizeCode();
        String codeVerifier = signInDto.getCodeVerifier();

        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");

        messageDigest.reset();
        messageDigest.update(codeVerifier.getBytes());
        String encodedCodeChallenge = String.format("%064x", new BigInteger(1, messageDigest.digest()));

        if(authorizeCodeAndChallengeMap.get(authorizeCode).equals(encodedCodeChallenge)){

            authorizeCodeAndChallengeMap.remove(authorizeCode);

            Map<String, Object> userAttributes = Map.of(
                    "email", email,
                    "provider", provider,
                    "providerId", providerId
            );

            String accessToken = jwtService.createAccessToken(email);
            String refreshToken = jwtService.createRefreshToken(email);

            jwtService.setAccessTokenOnHeader(response, accessToken);
            jwtService.setRefreshTokenOnHeader(response, refreshToken);

            User user = userRepository.findByEmailAndProvider(email,provider).orElse(null);

            response.setHeader("nickname", user != null ? user.getNickname() : null);
            response.setHeader("email", email);

            signIn(userAttributes, refreshToken);

        }
        else {
            throw new ForbiddenException();
        }

    }

    // --------------------------------------------------------------- //

    @KafkaProducer(topic = "resign")
    private void produceResign(String email, String nickname){
    }

    @Scheduled(fixedRate = 600000)
    public void deleteAuthorizeCodeAndChallengeMap(){
        authorizeCodeAndChallengeMap.clear();
    }
}
