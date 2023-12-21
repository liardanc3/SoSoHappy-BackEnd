package dev.sosohappy.monolithic.repository.rdbms;

import dev.sosohappy.monolithic.model.entity.FeedCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FeedCategoryRepository extends JpaRepository<FeedCategory, Long> {

    @Query("SELECT DISTINCT f.category FROM FeedCategory f")
    List<String> findDistinctCategory();
}
