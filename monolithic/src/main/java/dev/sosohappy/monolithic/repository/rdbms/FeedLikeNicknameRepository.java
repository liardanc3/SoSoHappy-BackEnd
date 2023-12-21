package dev.sosohappy.monolithic.repository.rdbms;

import dev.sosohappy.monolithic.model.entity.Feed;
import dev.sosohappy.monolithic.model.entity.FeedLikeNickname;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeedLikeNicknameRepository extends JpaRepository<FeedLikeNickname, Long> {

    Optional<FeedLikeNickname> findByFeedAndNickname(Feed feed, String nickname);

}
