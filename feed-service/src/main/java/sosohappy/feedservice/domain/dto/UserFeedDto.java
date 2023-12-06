package sosohappy.feedservice.domain.dto;

import lombok.Data;
import sosohappy.feedservice.domain.entity.Feed;
import sosohappy.feedservice.domain.entity.FeedCategory;
import sosohappy.feedservice.domain.entity.FeedImage;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserFeedDto {

    private String nickname;

    private String weather;

    private Long date;

    private Integer happiness;

    private String text;

    private Boolean isPublic;

    private List<String> categoryList;

    private List<Long> imageIdList;

    private List<String> likeNicknameList;

    public UserFeedDto(Feed feed){
        this.nickname = feed.getNickname();
        this.weather = feed.getWeather();
        this.date = feed.getDate();
        this.happiness = feed.getHappiness();
        this.text = feed.getText();
        this.isPublic = feed.getIsPublic();
        this.categoryList = feed.getFeedCategories().stream().map(FeedCategory::getCategory).collect(Collectors.toList());
        this.imageIdList = feed.getFeedImages().stream().map(FeedImage::getId).collect(Collectors.toList());
    }
}
