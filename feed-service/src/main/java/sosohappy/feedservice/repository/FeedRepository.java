package sosohappy.feedservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sosohappy.feedservice.domain.entity.Feed;

public interface FeedRepository extends JpaRepository<Feed, Long>, FeedQueryRepository {

}
