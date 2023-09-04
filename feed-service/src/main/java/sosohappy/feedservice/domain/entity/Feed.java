package sosohappy.feedservice.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sosohappy.feedservice.domain.dto.UpdateFeedDto;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Feed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_id")
    private Long id;

    @Column
    private String nickname;

    @Column
    private String weather;

    @Column
    private Long date;

    @Column
    private Integer happiness;

    @Column
    private String text;

    @Column
    private Boolean isPublic;

    @ElementCollection
    @CollectionTable(
            name = "feed_categories",
            joinColumns = @JoinColumn(name = "feed_id")
    )
    @Column
    private List<String> categoryList;

    @ElementCollection
    @CollectionTable(
            name = "feed_images",
            joinColumns = @JoinColumn(name = "feed_id")
    )
    @Column
    private List<String> imageList; // base64

    @ElementCollection
    @CollectionTable(
            name = "feed_likes",
            joinColumns = @JoinColumn(name = "feed_id")
    )
    @Column
    private List<String> likeNicknameList;


    // --------------------------------------- //
    public void updateFeed(UpdateFeedDto updateFeedDto){

    }

    public Feed(UpdateFeedDto updateFeedDto){
        this.weather = updateFeedDto.getWeather();
        this.date = updateFeedDto.getDate();
        this.text = updateFeedDto.getText();
        this.happiness = updateFeedDto.getHappiness();
        this.categoryList = updateFeedDto.getCategoryList();
        this.imageList = updateFeedDto.getImageList();
        this.isPublic = updateFeedDto.getIsPublic();
    }
}
