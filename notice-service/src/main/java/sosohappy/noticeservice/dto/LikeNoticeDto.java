package sosohappy.noticeservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikeNoticeDto {

    private String liker;
    private Double date;
}
