package sosohappy.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sosohappy.authservice.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByRefreshToken(String refreshToken);
    Optional<User> findByNickname(String nickname);
    Optional<User> findByEmailAndProvider(String email, String provider);
    Optional<String> findIntroductionByNickname(String nickname);
}
