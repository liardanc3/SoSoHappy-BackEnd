package sosohappy.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sosohappy.authservice.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByNickName(String name);
    Optional<User> findByEmail(String email);
    Optional<User> findByRefreshToken(String refreshToken);
}
