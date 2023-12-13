package sosohappy.feedservice.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sosohappy.feedservice.domain.entity.Feed;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long>, FeedQueryRepository {

    @EntityGraph(attributePaths = {"feedCategories"})
    List<Feed> findAll();

    @Modifying
    @Query("update Feed f set f.nickname = :after where f.nickname = :before")
    void updateFeedNickname(@Param("before") String before, @Param("after") String after);

    @Modifying
    void deleteByNickname(String nickname);

}
