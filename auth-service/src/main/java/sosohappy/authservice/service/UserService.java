package sosohappy.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sosohappy.authservice.entity.User;
import sosohappy.authservice.repository.UserRepository;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void save(Map<String, Object> userAttributes, String refreshToken) {
        userRepository.save(
                User.builder()
                        .email(String.valueOf(userAttributes.get("email")))
                        .nickName(String.valueOf(userAttributes.get("name")))
                        .provider(String.valueOf(userAttributes.get("provider")))
                        .providerId(String.valueOf(userAttributes.get("providerId")))
                        .refreshToken(refreshToken)
                        .build()
        );
    }
}
