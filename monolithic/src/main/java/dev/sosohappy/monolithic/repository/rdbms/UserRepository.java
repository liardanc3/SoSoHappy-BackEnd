package dev.sosohappy.monolithic.repository.rdbms;

import dev.sosohappy.monolithic.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByRefreshToken(String refreshToken);
    Optional<User> findByNickname(String nickname);
    Optional<User> findByEmailAndProvider(String email, String provider);

    @Query("SELECT u.deviceToken FROM User u WHERE u.nickname = :nickname")
    String findDeviceTokenByNickname(String nickname);
}
