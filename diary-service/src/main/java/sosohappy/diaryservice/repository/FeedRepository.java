package sosohappy.diaryservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sosohappy.diaryservice.domain.entity.Feed;

public interface FeedRepository extends JpaRepository<Feed, Long>, FeedQueryRepository {

}
