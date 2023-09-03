package sosohappy.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sosohappy.authservice.entity.*;
import sosohappy.authservice.kafka.KafkaProducer;
import sosohappy.authservice.repository.UserRepository;

import java.util.Map;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public void signIn(Map<String, Object> userAttributes, String refreshToken) {
        String email = String.valueOf(userAttributes.get("email"));
        String name = String.valueOf(userAttributes.get("name"));
        String provider = String.valueOf(userAttributes.get("provider"));
        String providerId = String.valueOf(userAttributes.get("providerId"));

        userRepository.findByEmail(email)
                        .ifPresentOrElse(
                                user -> user.updateRefreshToken(refreshToken),
                                () -> userRepository.save(
                                        User.builder()
                                                .email(email)
                                                .nickname(name)
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

    public SetProfileDto setProfile(UserDto userDto) {
        return userRepository.findByEmail(userDto.getEmail())
                .map(user -> {
                    user.updateProfile(userDto);
                    return SetProfileDto.builder()
                            .email(user.getEmail())
                            .success(true)
                            .build();
                })
                .orElseGet(() ->
                        SetProfileDto.builder()
                                .email(userDto.getEmail())
                                .success(false)
                                .build()
                );
    }


}
