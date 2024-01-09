package dev.sosohappy.monolithic.repository.rdbms;

import dev.sosohappy.monolithic.model.entity.Feed;
import dev.sosohappy.monolithic.model.entity.FeedLikeNickname;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FeedLikeNicknameRepository extends JpaRepository<FeedLikeNickname, Long> {

    Optional<FeedLikeNickname> findByFeedAndNickname(Feed feed, String nickname);

    @Modifying
    @Query("update FeedLikeNickname fn set fn.nickname = :after where fn.nickname = :before")
    void updateFeedLikeNickname(String before, String after);

}
