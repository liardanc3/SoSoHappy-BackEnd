package sosohappy.feedservice.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sosohappy.feedservice.domain.dto.HappinessAndCategoryDto;
import sosohappy.feedservice.domain.entity.Feed;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long>, FeedQueryRepository {

    @EntityGraph(attributePaths = {"categoryList"})
    @Query("SELECT NEW sosohappy.feedservice.domain.dto.HappinessAndCategoryDto(f) FROM Feed f")
    List<HappinessAndCategoryDto> findHappinessAndCategoryDtoAll();

    @Query("SELECT DISTINCT c FROM Feed f JOIN f.categoryList c")
    List<String> findAllCategories();

    void deleteByNickname(String nickname);

    List<Feed> findByLikeNicknameSetContaining(String nickname);
}
