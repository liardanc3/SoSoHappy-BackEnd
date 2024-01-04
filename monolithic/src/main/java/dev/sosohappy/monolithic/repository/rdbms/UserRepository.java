package dev.sosohappy.monolithic.repository.rdbms;

import dev.sosohappy.monolithic.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying
    @Query(value = "DELETE FROM user_block WHERE user_id = :srcUserId AND block_user_id = :dstUserId", nativeQuery = true)
    void deleteBlockUser(Long srcUserId, Long dstUserId);

    @Modifying
    @Query(value = "INSERT INTO user_block (user_id, block_user_id) values (:srcUserId, :dstUserId)", nativeQuery = true)
    void insertBlockUser(Long srcUserId, Long dstUserId);
}
