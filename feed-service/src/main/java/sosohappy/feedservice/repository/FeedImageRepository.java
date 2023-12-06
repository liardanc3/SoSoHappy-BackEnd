package sosohappy.feedservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sosohappy.feedservice.domain.dto.ImageDto;
import sosohappy.feedservice.domain.entity.FeedImage;

public interface FeedImageRepository extends JpaRepository<FeedImage, Long> {

    @Query("SELECT NEW sosohappy.feedservice.domain.dto.ImageDto(fi) FROM FeedImage fi WHERE fi.id = :imageId")
    ImageDto findImageById(Long imageId);

}
