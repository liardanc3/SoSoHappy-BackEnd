package sosohappy.feedservice.domain.dto;

import lombok.Data;
import sosohappy.feedservice.domain.entity.Feed;
import sosohappy.feedservice.domain.entity.FeedCategory;
import sosohappy.feedservice.domain.entity.FeedImage;
import sosohappy.feedservice.domain.entity.FeedLikeNickname;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class OtherFeedDto {

    private String nickname;

    private String weather;

    private Long date;

    private Integer happiness;

    private String text;

    private List<String> categoryList;

    private List<Long> imageIdList;

    private Boolean isLiked;

    public OtherFeedDto(Feed feed, List<Long> imageIdList, List<FeedCategory> categoryList, List<FeedLikeNickname> likeNicknameList, String nickname){
        this.nickname = feed.getNickname();
        this.weather = feed.getWeather();
        this.date = feed.getDate();
        this.happiness = feed.getHappiness();
        this.text = feed.getText();
        this.categoryList = categoryList.stream().map(FeedCategory::getCategory).filter(category -> !category.equals("")).collect(Collectors.toList());
        this.imageIdList = imageIdList.stream().filter(imageId -> imageId != 0).collect(Collectors.toList());
        this.isLiked = likeNicknameList.stream().map(FeedLikeNickname::getNickname).toList().contains(nickname);
    }

}
