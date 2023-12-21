package sosohappy.feedservice.domain.dto;

import lombok.Data;
import sosohappy.feedservice.domain.entity.Feed;
import sosohappy.feedservice.domain.entity.FeedCategory;
import sosohappy.feedservice.domain.entity.FeedImage;
import sosohappy.feedservice.domain.entity.FeedLikeNickname;

import java.util.List;
import java.util.Objects;
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

    public UserFeedDto(Feed feed, List<Long> imageIdList, List<FeedCategory> categoryList, List<FeedLikeNickname> likeNicknameList){
        this.nickname = feed.getNickname();
        this.weather = feed.getWeather();
        this.date = feed.getDate();
        this.happiness = feed.getHappiness();
        this.text = feed.getText();
        this.isPublic = feed.getIsPublic();
        this.likeNicknameList = likeNicknameList.stream().map(FeedLikeNickname::getNickname)
                .filter(Objects::nonNull)
                .filter(likeNickname -> !likeNickname.equals(""))
                .distinct()
                .toList();
        this.categoryList = categoryList.stream().map(FeedCategory::getCategory)
                .filter(Objects::nonNull)
                .filter(category -> !category.equals(""))
                .distinct()
                .toList();
        this.imageIdList = imageIdList.stream()
                .filter(imageId -> imageId != 0)
                .distinct()
                .toList();
    }

}
