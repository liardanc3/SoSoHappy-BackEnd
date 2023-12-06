package sosohappy.feedservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sosohappy.feedservice.domain.entity.Feed;
import sosohappy.feedservice.domain.entity.FeedLikeNickname;

import java.util.Optional;

public interface FeedLikeNicknameRepository extends JpaRepository<FeedLikeNickname, Long> {

    Optional<FeedLikeNickname> findByFeedAndNickname(Feed feed, String nickname);

}
