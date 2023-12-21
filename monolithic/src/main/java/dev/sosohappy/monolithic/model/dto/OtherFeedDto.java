package dev.sosohappy.monolithic.model.dto;

import dev.sosohappy.monolithic.model.entity.Feed;
import dev.sosohappy.monolithic.model.entity.FeedCategory;
import dev.sosohappy.monolithic.model.entity.FeedLikeNickname;
import lombok.Data;

import java.util.List;

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
        this.categoryList = categoryList.stream().map(FeedCategory::getCategory).filter(category -> !category.equals("")).distinct().toList();
        this.imageIdList = imageIdList.stream().filter(imageId -> imageId != 0).distinct().toList();
        this.isLiked = likeNicknameList.stream().map(FeedLikeNickname::getNickname).toList().contains(nickname);
    }

}
