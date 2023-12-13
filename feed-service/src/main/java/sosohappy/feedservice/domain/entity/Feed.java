package sosohappy.feedservice.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sosohappy.feedservice.domain.dto.UpdateFeedDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "MEDIUMTEXT")
    private String text;

    @Column
    private Boolean isPublic;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedCategory> feedCategories = new ArrayList<>();

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedLikeNickname> feedLikeNicknames = new ArrayList<>();

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedImage> feedImages = new ArrayList<>();

    // ------------------------------------------------------------------------------------------------ //

    public void updateFeed(UpdateFeedDto updateFeedDto){
        this.feedCategories.clear();
        this.feedImages.clear();

        this.nickname = updateFeedDto.getNickname();
        this.weather = updateFeedDto.getWeather();
        this.date = updateFeedDto.getDate();
        this.text = updateFeedDto.getText();
        this.happiness = updateFeedDto.getHappiness();
        this.isPublic = updateFeedDto.getIsPublic();

        updateFeedDto.getCategoryList().forEach(category -> this.feedCategories.add(new FeedCategory(this, category)));
        updateFeedDto.getImageList().forEach(image -> {
            try {
                this.feedImages.add(new FeedImage(this, image.getBytes()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
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
        this.feedCategories = updateFeedDto.getCategoryList().stream().map(category -> new FeedCategory(this, category)).collect(Collectors.toList());
        this.isPublic = updateFeedDto.getIsPublic();

        this.feedImages = new ArrayList<>();
        if(updateFeedDto.getImageList() != null && !updateFeedDto.getImageList().isEmpty()){
            for (MultipartFile multipartFile : updateFeedDto.getImageList()) {
                this.feedImages.add(new FeedImage(this, multipartFile.getBytes()));
            }
        }
    }

    public void like(String nickname){
        this.feedLikeNicknames.add(new FeedLikeNickname(this, nickname));
    }

}
