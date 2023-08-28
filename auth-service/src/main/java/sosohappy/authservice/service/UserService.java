package sosohappy.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sosohappy.authservice.entity.ResignDto;
import sosohappy.authservice.entity.User;
import sosohappy.authservice.entity.UserDto;
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
                            .httpStatus(200)
                            .email(email)
                            .build();
                })
                .orElseGet(() ->
                        ResignDto.builder()
                                .httpStatus(500)
                                .email(email)
                                .build()
                );
    }

    public Boolean checkDuplicateNickname(String nickname) {
        return userRepository.findByNickname(nickname).isEmpty();
    }

    public void setProfile(UserDto userDto) {
        userRepository.findByEmail(userDto.getEmail())
                .ifPresent(user -> user.updateProfile(userDto));
    }
}
