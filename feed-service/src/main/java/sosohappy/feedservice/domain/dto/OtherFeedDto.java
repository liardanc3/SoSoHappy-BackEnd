package sosohappy.feedservice.domain.dto;

import sosohappy.feedservice.domain.entity.Feed;

import java.util.List;

public class OtherFeedDto {

    private String nickname;

    private String weather;

    private Long date;

    private Integer happiness;

    private String text;

    private List<String> categoryList;

    private List<byte[]> imageList;

    private Boolean isLiked;

    public OtherFeedDto(Feed feed, String nickname){
        this.nickname = feed.getNickname();
        this.weather = feed.getWeather();
        this.date = feed.getDate();
        this.happiness = feed.getHappiness();
        this.text = feed.getText();
        this.categoryList = feed.getCategoryList();
        this.imageList = feed.getImageList();

        this.isLiked = feed.getLikeNicknameList().contains(nickname);
    }
}
