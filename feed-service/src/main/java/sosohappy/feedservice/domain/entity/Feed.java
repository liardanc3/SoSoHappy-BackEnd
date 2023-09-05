package sosohappy.feedservice.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;
import sosohappy.feedservice.domain.dto.UpdateFeedDto;

import java.util.ArrayList;
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

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "feed_categories",
            joinColumns = @JoinColumn(name = "feed_id")
    )
    @Column
    private List<String> categoryList;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "feed_images",
            joinColumns = @JoinColumn(name = "feed_id")
    )
    @Column(columnDefinition = "MEDIUMBLOB")
    private List<byte[]> imageList = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "feed_likes",
            joinColumns = @JoinColumn(name = "feed_id")
    )
    @Column
    private List<String> likeNicknameList = new ArrayList<>();


    // --------------------------------------- //

    @SneakyThrows
    public void updateFeed(UpdateFeedDto updateFeedDto){
        this.nickname = updateFeedDto.getNickname();
        this.weather = updateFeedDto.getWeather();
        this.date = updateFeedDto.getDate();
        this.text = updateFeedDto.getText();
        this.happiness = updateFeedDto.getHappiness();
        this.categoryList = updateFeedDto.getCategoryList();
        this.isPublic = updateFeedDto.getIsPublic();

        this.imageList.clear();
        if(updateFeedDto.getImageList() != null && !updateFeedDto.getImageList().isEmpty()){
            for (MultipartFile multipartFile : updateFeedDto.getImageList()) {
                this.imageList.add(multipartFile.getBytes());
            }
        }
    }

    public static Feed updateIsPublic(Feed feed){
        feed.isPublic = !feed.isPublic;
        return feed;
    }

    @SneakyThrows
    public Feed(UpdateFeedDto updateFeedDto){
        this.nickname = updateFeedDto.getNickname();
        this.weather = updateFeedDto.getWeather();
        this.date = updateFeedDto.getDate();
        this.text = updateFeedDto.getText();
        this.happiness = updateFeedDto.getHappiness();
        this.categoryList = updateFeedDto.getCategoryList();
        this.isPublic = updateFeedDto.getIsPublic();

        this.imageList = new ArrayList<>();
        if(updateFeedDto.getImageList() != null && !updateFeedDto.getImageList().isEmpty()){

            List<MultipartFile> imageList = updateFeedDto.getImageList();

            for (MultipartFile multipartFile : imageList) {
                this.imageList.add(multipartFile.getBytes());
            }
        }
    }
}
