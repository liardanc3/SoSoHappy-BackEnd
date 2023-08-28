package sosohappy.authservice.oauth2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sosohappy.authservice.entity.ResignDto;
import sosohappy.authservice.entity.User;
import sosohappy.authservice.kafka.KafkaProducer;
import sosohappy.authservice.repository.UserRepository;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional
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
                                                .nickName(name)
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
}
