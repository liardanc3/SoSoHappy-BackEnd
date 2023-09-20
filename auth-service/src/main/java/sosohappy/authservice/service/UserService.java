package sosohappy.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import sosohappy.authservice.entity.*;
import sosohappy.authservice.kafka.KafkaProducer;
import sosohappy.authservice.repository.UserRepository;

import java.util.Map;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final ObjectProvider<UserService> userServiceProvider;

    public void signIn(Map<String, Object> userAttributes, String refreshToken) {
        String email = String.valueOf(userAttributes.get("email"));
        String provider = String.valueOf(userAttributes.get("provider"));
        String providerId = String.valueOf(userAttributes.get("providerId"));

        userRepository.findByEmail(email)
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
                    userServiceProvider.getObject().produceResign(user.getNickname());
                    userRepository.delete(user);
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

    // --------------------------------------------------------------- //

    @KafkaProducer(topic = "resign")
    private void produceResign(String nickname){
    }
}
