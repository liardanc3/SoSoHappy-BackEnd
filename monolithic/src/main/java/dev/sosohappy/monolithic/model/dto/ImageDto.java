package dev.sosohappy.monolithic.model.dto;

import dev.sosohappy.monolithic.model.entity.FeedImage;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageDto {

    private byte[] image;

    public ImageDto(FeedImage feedImage) {
        this.image = feedImage.getImage();
    }
}
