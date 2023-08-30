package sosohappy.diaryservice.domain.dto;

import jakarta.persistence.*;
import lombok.Data;
import sosohappy.diaryservice.domain.entity.Feed;
import sosohappy.diaryservice.domain.entity.Weather;

import java.util.List;

@Data
public class FeedDto {

    private String weather;

    private Long date;

    private Integer happiness;

    private String text;

    private Boolean isPublic;

    private List<String> categoryList;

    private List<String> imageList;

    private List<String> likeNicknameList;

    public FeedDto(Feed feed){
        this.weather = feed.getWeather().name();
        this.date = feed.getDate();
        this.happiness = feed.getHappiness();
        this.text = feed.getText();
        this.isPublic = feed.getIsPublic();
        this.categoryList = feed.getCategoryList();
        this.imageList = feed.getImageList();
    }
}
