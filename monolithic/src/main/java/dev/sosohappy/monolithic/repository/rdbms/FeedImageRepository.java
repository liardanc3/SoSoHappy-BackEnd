package dev.sosohappy.monolithic.repository.rdbms;

import dev.sosohappy.monolithic.model.dto.ImageDto;
import dev.sosohappy.monolithic.model.entity.FeedImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeedImageRepository extends JpaRepository<FeedImage, Long> {

    @Query("SELECT NEW dev.sosohappy.monolithic.model.dto.ImageDto(fi) FROM FeedImage fi WHERE fi.id = :imageId")
    ImageDto findImageById(Long imageId);

}
