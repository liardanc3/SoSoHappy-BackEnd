package sosohappy.feedservice.domain.dto;

import lombok.Data;
import sosohappy.feedservice.domain.entity.Feed;

import java.util.List;

@Data
public class UserFeedDto {

    private String nickname;

    private String weather;

    private Long date;

    private Integer happiness;

    private String text;

    private Boolean isPublic;

    private List<String> categoryList;

    private List<byte[]> imageList;

    private List<String> likeNicknameList;

    public UserFeedDto(Feed feed){
        this.nickname = feed.getNickname();
        this.weather = feed.getWeather();
        this.date = feed.getDate();
        this.happiness = feed.getHappiness();
        this.text = feed.getText();
        this.isPublic = feed.getIsPublic();
        this.categoryList = feed.getCategoryList();
        this.imageList = feed.getImageList();
    }
}
