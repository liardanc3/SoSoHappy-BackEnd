package sosohappy.feedservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sosohappy.feedservice.domain.entity.FeedCategory;

import java.util.List;

public interface FeedCategoryRepository extends JpaRepository<FeedCategory, Long> {

    @Query("SELECT DISTINCT f.category FROM FeedCategory f")
    List<String> findDistinctCategory();
}
