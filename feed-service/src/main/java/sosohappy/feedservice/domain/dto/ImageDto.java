package sosohappy.feedservice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import sosohappy.feedservice.domain.entity.FeedImage;

@Data
@AllArgsConstructor
public class ImageDto {

    private byte[] image;

    public ImageDto(FeedImage feedImage) {
        this.image = feedImage.getImage();
    }
}
