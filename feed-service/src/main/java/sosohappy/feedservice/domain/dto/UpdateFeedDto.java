package sosohappy.feedservice.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateFeedDto {

    private String nickname;

    private Long date;
    private String weather;
    private Integer happiness;

    private List<String> categoryList;
    private String text;
    private List<String> imageList;
    private Boolean isPublic;

}
